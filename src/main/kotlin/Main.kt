package org.histograms

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.AttributeKey.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.metrics.Aggregation
import io.opentelemetry.sdk.metrics.InstrumentSelector
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.View
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import java.time.Duration
import java.util.Random
import java.util.logging.Logger


fun main() {
    val logger = Logger.getLogger("histogram-example")

    val normalHistogramName = "job.duration"
    val exponentialHistogramCustomName = "$normalHistogramName.exponential.custom"
    val exponentialHistogramDefaultsName = "$normalHistogramName.exponential.defaults"

    val sdkMeterProvider: SdkMeterProvider =
        SdkMeterProvider.builder()
            // apply a custom maxScale
            .registerView(
                InstrumentSelector.builder().setName(exponentialHistogramCustomName).build(),
                View.builder()
                    .setAggregation(Aggregation.base2ExponentialBucketHistogram(160, 4))
                    .build()
            )
            // Use defaults (maxBuckets: 160 maxScale: 20)
            .registerView(
                InstrumentSelector.builder().setName(exponentialHistogramDefaultsName).build(),
                View.builder()
                    .setAggregation(Aggregation.base2ExponentialBucketHistogram())
                    .build()
            )
            .registerMetricReader(
                PeriodicMetricReader.builder(
                    OtlpGrpcMetricExporter.builder().build()
                )
                    .setInterval(Duration.ofSeconds(10))
                    .build()
            )
            .build()

    val otel: OpenTelemetry = OpenTelemetrySdk.builder().setMeterProvider(sdkMeterProvider).build()
    val meter: Meter = otel.getMeter("io.opentelemetry.example.metrics")

    val metrics = listOf(normalHistogramName, exponentialHistogramDefaultsName, exponentialHistogramCustomName)
        .map {
            meter
                .histogramBuilder(it)
                .ofLongs()
                .setDescription("A distribution of job execution time")
                .setUnit("seconds")
                .build()
        }

    val rand = Random()
    val attrs: Attributes =
        Attributes.of(
            stringKey("job"), "update_database",
            stringKey("env"), "production",
        )

    logger.info("Recording points")
    while (true) {
        val value: Long = rand.nextLong(115)
        metrics.forEach {
            it.record(value, attrs)
        }
        Thread.sleep(1000)
    }
}