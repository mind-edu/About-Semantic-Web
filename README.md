# 关于 ```语义Web``` 资料整理

- 知识图谱技术的前身

[CodeWisdom平台API知识图谱服务系统全新上线了](https://mp.weixin.qq.com/s/8ReWM8AcN170LA8o6zjrsw)

- [Protege](https://protege.stanford.edu/)

## 运行说明

```
# 在终端运行命令
fuseki-server
```

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX math:<http://www.semanticweb.org/chengboya/ontologies/%s#>

SELECT ?resource ?quality_value

WHERE {
    math:函数基本求导法则 math:resource ?resource.?resource math:quality_is ?quality_value
}
ORDER BY DESC(?quality_value)
LIMIT 10
```