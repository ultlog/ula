<h1 align="center">ultlog-api</h1>
<p align="center">
  <a target="_blank" href="https://github.com/ultlog/ula/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue"></a>
  <a target="_blank" href="https://github.com/ultlog/ula/pulls"><img src=https://img.shields.io/badge/pr-welcome-green"></a>
  <a target="_blank" href="https://github.com/ultlog/ula/releases/"><img src="https://img.shields.io/github/v/release/ultlog/ula"></a>
  <a target="_blank" href="https://github.com/ultlog/ula/pulls?q=is%3Apr+is%3Aclosed"><img src="https://img.shields.io/github/issues-pr-closed/ultlog/ula"></a>
</p>
<p align="center">
The abbreviation of ultlog-api is ula, which is a http server to save logs to es and query log.
</p>
   
<p align="center">
  <a href="https://ultlog.com" target="_blank">
    文档
  </a>
  / 
  <a href="https://github.com/ultlog/ulu/" target="_blank">
    ultlog-ui
  </a>
  / 
  <a href="https://github.com/ultlog/collector" target="_blank">
    collector
  </a>
  /
  <a href="https://github.com/ultlog/searcher" target="_blank">
    searcher
  </a>
</p>

## Need
Requires [Elasticsearch](https://www.elastic.co/) which version is 7.x .

## Install
### Download
click[this](https://github.com/ultlog/ula/releases) to download ula.jar.

### Run
````shell script
java -jar -D"ultlog.es.address.host={es-ip}" -D"ultlog.es.address.port={es-port}"   ula-0.0.1.jar
 ````
Replace es-ip and es-port.
### 个性化

If you need to modify the configuration information such as port, ip.You can add it in the startup item.
````shell script
-Dspring.port=8888
````

Or create a configuration file in the same level directory, it is more convenient to modify the configuration.
