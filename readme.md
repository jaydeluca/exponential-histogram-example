# Exponential Histogram Example

## Context

Lab environment to experiment with OpenTelemetry Exponential Histograms.

Additional References:

* [OTel Documentation for the ExponentialHistogram Data model](https://opentelemetry.io/docs/specs/otel/metrics/data-model/#exponentialhistogram)
* [OTel Documentation for Histogram Aggregations](https://opentelemetry.io/docs/specs/otel/metrics/sdk/#histogram-aggregations)
* [Exponential Histograms: Better Data, Zero Configuration](https://opentelemetry.io/blog/2022/exponential-histograms/) 2022 Blog post by Jack Berg
    * [Code example associated with the blog post by Jack Berg](https://github.com/jack-berg/newrelic-opentelemetry-examples/commit/2681bf25518c02f4e5830f89254c736e0959d306)

This lab has a simple program written in Kotlin that uses the OpenTelemetry java SDK to create 3 histograms, and then 
using views, leverage the Exponential Histogram functionality for 2 of the metrics. The metrics are exported over gRPC to 
`the OTel collector -> prometheus -> grafana` where they can be visualized.


## Run lab

Uses the [grafana/otel-lgtm](https://github.com/grafana/docker-otel-lgtm) container 
to spin up all the observability tools (OTel collector, prometheus, grafana)

`docker compose up -d`

@TODO: containerize app



Access Grafana:

```
http://localhost:3000/
```

Login with `admin/admin`

Navigate to `Dashboards` -> [`Exponential Histograms`](http://localhost:3000/d/fdj5lsyfzhatcc/exponential-histograms?orgId=1&refresh=5s)

![dashboard](dashboard.png)

## Notes / Troubleshooting

### Missing Grafana dashboard

The way we are linking the grafana dashboard sourcecode in the docker-compose file relies on the grafana version in the 
path:

```yaml
volumes:
  - ./grafana/dashboards.yaml:/otel-lgtm/grafana-v10.4.1/conf/provisioning/dashboards/dashboards.yaml
```

You may need to docker exec into the image to see the version is different and update the docker-compose.yml accordingly.

Jump into the container:

`docker exec -it $(docker ps | grep otel-lgtm | awk '{print $1}') bash`

Check id:

`ls -la | grep grafana-v`

Should result in the following, which is what should be used in the path:

`grafana-v10.4.1`


@TODO: find a better way to make this not rely on the version in the path