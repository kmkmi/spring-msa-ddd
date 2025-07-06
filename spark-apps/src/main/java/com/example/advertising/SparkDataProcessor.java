package com.example.advertising;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;


public class SparkDataProcessor {
    
    public static void main(String[] args) {
        // 환경변수에서 설정 가져오기
        String sparkMaster = System.getenv("SPARK_MASTER");
        if (sparkMaster == null) {
            sparkMaster = "spark://spark-master:7077"; // 기본값
        }
        
        String executorMemory = System.getenv("SPARK_EXECUTOR_MEMORY");
        if (executorMemory == null) {
            executorMemory = "2g"; // 기본값
        }
        
        String driverMemory = System.getenv("SPARK_DRIVER_MEMORY");
        if (driverMemory == null) {
            driverMemory = "1g"; // 기본값
        }
        
        // Spark 설정
        SparkConf conf = new SparkConf()
            .setAppName("Advertising Data Processor")
            .setMaster(sparkMaster)
            .set("spark.executor.memory", executorMemory)
            .set("spark.driver.memory", driverMemory);
        
        SparkSession spark = SparkSession.builder()
            .config(conf)
            .getOrCreate();
        
        
        try {
            // 1. 광고 이벤트 데이터 로드
            Dataset<Row> adEvents = loadAdEvents(spark);
            
            // 2. 데이터 정제 및 변환
            Dataset<Row> cleanedEvents = cleanAndTransformData(adEvents);
            
            // 3. 일별 메트릭 집계
            Dataset<Row> dailyMetrics = aggregateDailyMetrics(cleanedEvents);
            
            // 4. 캠페인별 성과 분석
            Dataset<Row> campaignPerformance = analyzeCampaignPerformance(cleanedEvents);
            
            // 5. 디바이스별 분석
            Dataset<Row> deviceAnalysis = analyzeByDevice(cleanedEvents);
            
            // 6. 지역별 분석
            Dataset<Row> locationAnalysis = analyzeByLocation(cleanedEvents);
            
            // 7. 결과 저장
            saveResults(dailyMetrics, campaignPerformance, deviceAnalysis, locationAnalysis);
            
            System.out.println("Spark 데이터 처리 완료");
            
        } catch (Exception e) {
            System.err.println("Spark 데이터 처리 실패: " + e.getMessage());
            e.printStackTrace();
        } finally {
            spark.close();
        }
    }
    
    /**
     * 광고 이벤트 데이터 로드
     */
    private static Dataset<Row> loadAdEvents(SparkSession spark) {
        // 환경변수에서 데이터 경로 가져오기
        String inputPath = System.getenv("SPARK_DATA_INPUT_PATH");
        if (inputPath == null) {
            inputPath = "/opt/data/ad-events"; // 기본값
        }
        
        // Kafka에서 데이터를 읽어오거나 파일에서 로드
        // 예시: JSON 파일에서 로드
        return spark.read()
            .option("multiline", true)
            .json(inputPath + "/*.json");
    }
    
    /**
     * 데이터 정제 및 변환
     */
    private static Dataset<Row> cleanAndTransformData(Dataset<Row> events) {
        return events
            .filter(functions.col("advertisementId").isNotNull())
            .filter(functions.col("campaignId").isNotNull())
            .filter(functions.col("eventType").isNotNull())
            .withColumn("timestamp", functions.to_timestamp(functions.col("timestamp")))
            .withColumn("date", functions.date_format(functions.col("timestamp"), "yyyy-MM-dd"))
            .withColumn("hour", functions.hour(functions.col("timestamp")))
            .withColumn("isViewable", 
                functions.when(functions.col("isViewable").isNull(), false)
                .otherwise(functions.col("isViewable")));
    }
    
    /**
     * 일별 메트릭 집계
     */
    private static Dataset<Row> aggregateDailyMetrics(Dataset<Row> events) {
        return events
            .groupBy("date", "advertisementId", "campaignId")
            .agg(
                functions.count(functions.when(functions.col("eventType").equalTo("IMPRESSION"), 1)).as("impressions"),
                functions.count(functions.when(functions.col("eventType").equalTo("CLICK"), 1)).as("clicks"),
                functions.count(functions.when(functions.col("eventType").equalTo("CONVERSION"), 1)).as("conversions"),
                functions.sum(functions.when(functions.col("eventType").equalTo("CLICK"), 
                    functions.col("bidAmount")).otherwise(0)).as("totalSpend"),
                functions.avg(functions.when(functions.col("eventType").equalTo("IMPRESSION"), 
                    functions.col("duration")).otherwise(0)).as("avgDuration"),
                functions.count(functions.when(functions.col("isViewable").equalTo(true), 1)).as("viewableImpressions")
            )
            .withColumn("ctr", 
                functions.when(functions.col("impressions").gt(0), 
                    functions.col("clicks").cast(DataTypes.DoubleType).divide(functions.col("impressions")))
                .otherwise(0))
            .withColumn("cvr", 
                functions.when(functions.col("clicks").gt(0), 
                    functions.col("conversions").cast(DataTypes.DoubleType).divide(functions.col("clicks")))
                .otherwise(0))
            .withColumn("viewabilityRate", 
                functions.when(functions.col("impressions").gt(0), 
                    functions.col("viewableImpressions").cast(DataTypes.DoubleType).divide(functions.col("impressions")))
                .otherwise(0));
    }
    
    /**
     * 캠페인별 성과 분석
     */
    private static Dataset<Row> analyzeCampaignPerformance(Dataset<Row> events) {
        return events
            .groupBy("campaignId", "date")
            .agg(
                functions.count(functions.when(functions.col("eventType").equalTo("IMPRESSION"), 1)).as("impressions"),
                functions.count(functions.when(functions.col("eventType").equalTo("CLICK"), 1)).as("clicks"),
                functions.count(functions.when(functions.col("eventType").equalTo("CONVERSION"), 1)).as("conversions"),
                functions.sum(functions.when(functions.col("eventType").equalTo("CLICK"), 
                    functions.col("bidAmount")).otherwise(0)).as("totalSpend"),
                functions.countDistinct("userId").as("uniqueUsers")
            )
            .withColumn("ctr", 
                functions.when(functions.col("impressions").gt(0), 
                    functions.col("clicks").cast(DataTypes.DoubleType).divide(functions.col("impressions")))
                .otherwise(0))
            .withColumn("cpc", 
                functions.when(functions.col("clicks").gt(0), 
                    functions.col("totalSpend").divide(functions.col("clicks")))
                .otherwise(0))
            .withColumn("cpm", 
                functions.when(functions.col("impressions").gt(0), 
                    functions.col("totalSpend").multiply(1000).divide(functions.col("impressions")))
                .otherwise(0));
    }
    
    /**
     * 디바이스별 분석
     */
    private static Dataset<Row> analyzeByDevice(Dataset<Row> events) {
        return events
            .groupBy("deviceType", "date")
            .agg(
                functions.count(functions.when(functions.col("eventType").equalTo("IMPRESSION"), 1)).as("impressions"),
                functions.count(functions.when(functions.col("eventType").equalTo("CLICK"), 1)).as("clicks"),
                functions.count(functions.when(functions.col("eventType").equalTo("CONVERSION"), 1)).as("conversions"),
                functions.countDistinct("userId").as("uniqueUsers")
            )
            .withColumn("ctr", 
                functions.when(functions.col("impressions").gt(0), 
                    functions.col("clicks").cast(DataTypes.DoubleType).divide(functions.col("impressions")))
                .otherwise(0));
    }
    
    /**
     * 지역별 분석
     */
    private static Dataset<Row> analyzeByLocation(Dataset<Row> events) {
        return events
            .groupBy("location", "date")
            .agg(
                functions.count(functions.when(functions.col("eventType").equalTo("IMPRESSION"), 1)).as("impressions"),
                functions.count(functions.when(functions.col("eventType").equalTo("CLICK"), 1)).as("clicks"),
                functions.count(functions.when(functions.col("eventType").equalTo("CONVERSION"), 1)).as("conversions"),
                functions.countDistinct("userId").as("uniqueUsers")
            )
            .withColumn("ctr", 
                functions.when(functions.col("impressions").gt(0), 
                    functions.col("clicks").cast(DataTypes.DoubleType).divide(functions.col("impressions")))
                .otherwise(0));
    }
    
    /**
     * 결과 저장
     */
    private static void saveResults(Dataset<Row> dailyMetrics, Dataset<Row> campaignPerformance, 
                                  Dataset<Row> deviceAnalysis, Dataset<Row> locationAnalysis) {
        
        // 환경변수에서 출력 경로 가져오기
        String outputPath = System.getenv("SPARK_DATA_OUTPUT_PATH");
        if (outputPath == null) {
            outputPath = "/opt/data/results"; // 기본값
        }
        
        // Parquet 형식으로 저장
        dailyMetrics.write()
            .mode("overwrite")
            .parquet(outputPath + "/daily-metrics");
        
        campaignPerformance.write()
            .mode("overwrite")
            .parquet(outputPath + "/campaign-performance");
        
        deviceAnalysis.write()
            .mode("overwrite")
            .parquet(outputPath + "/device-analysis");
        
        locationAnalysis.write()
            .mode("overwrite")
            .parquet(outputPath + "/location-analysis");
        
        // CSV 형식으로도 저장 (보고서용)
        dailyMetrics.write()
            .mode("overwrite")
            .option("header", true)
            .csv(outputPath + "/csv/daily-metrics");
        
        campaignPerformance.write()
            .mode("overwrite")
            .option("header", true)
            .csv(outputPath + "/csv/campaign-performance");
    }
} 