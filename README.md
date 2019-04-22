#一、 jena 包下载与开发环境配置
1. 从官网下载 jena 包 apache-jana-2.7.*.tar.gz。解压。
2. 新建一个Java Project。
3. 在Project Structure里面找到library并将jena包解压后的jar包添加进去

#二、 简单使用
我们先看下面一个例子。这是一个 people 资源。
RDF 中关于人的信息用vcard 来表示比较合适。关于 RDF 中 vCard 的更多内容参考http://www.w3.org/TR/vcard-rdf/。
这个例子中，资源 http://.../JohnSmith 表示一个人。这个人的全名是 John Smith，即 vcard:FN 属性的值是 John Smith。在 Jena 中，资源用 Resource 类来表示，其属性用 Property 类来表示。而整体模型用Model 类来表示，即上图就是一个Model。一个 Model 对象可以包含多个资源。

上面所描述的资源使用 jena 编程表示： `Introduction.java`

其中， ModelFactory 类是一个Model 工厂，用于创建model 对象。我们可以使用 Model 的createResource 方法在model 中创建一个资源，并可以使用资源的 addProperty 方法添加属性。


