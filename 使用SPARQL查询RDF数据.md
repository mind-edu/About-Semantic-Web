# 使用 SPARQL 查询 RDF 数据

在 “大规模数据集成：使用 RDF 创建数据网络” 中，您了解了资源描述框架：一种万维网联盟 (W3C) 标准，它定义了一种基于图形的模型来支持来自几乎无限多个来源的完全可移植、灵活的数据。大规模连接数据的能力非常强大，但从某种程度上讲，您可能还希望询问有关 RDF 数据的问题。该技术需要一种理解 RDF 图形结构和基于网络的标识符的查询语言。

在 RDF 模型诞生很久以后，才出现了查询 RDF 数据的标准化方式。已在使用的专用查询语言有 6 种或更多，每种语言都有着自己的独特性和不兼容性。这种情形不利于对不同的 RDF 存储系统使用通用查询，使可移植数据的愿景大打折扣。幸运的是，W3C 在 2008 年发布了所谓的 SPARQL 协议和 RDF 查询语言 (SPARQL)。通过使用 SPARQL 客户端，通过将数据拉入到客户端，用户可以在本地执行查询，或者将查询推送到服务器来通过 SPARQL 协议远程执行查询。您可以在自己的数据或其他人的数据上使用同样的查询语法。


大规模数据集成 系列的第 2 期将介绍 SPARQL：后面几期用于探索 开放式生命周期协作服务 计划如何使用语义 Web 技术的基础层之一。参见 下载 部分，以获取本文的代码示例。

## 基本 SPARQL 查询

许多客户端库或应用程序都可以执行 SPARQL 查询。这里将重点介绍如何使用来自 Apache Jena 的 sparql 命令行工具。要对本地文件运行查询，可以键入一下内容：

```
sparql --query query.rq --data basic.nt
```

query.rq 文件包含您想要运行的查询。.rq 文件扩展名是一种约定，不是必需的。数据文件的内容可以是具有某种标准格式的 RDF。只要您使用一种适合序列化格式的标准后缀（例如，.ttl 用于 Turtle，.rdf 用于 RDF/XML，.nt 用于 N-Triples），sparql 工具就会正确地解析该格式。如果使用 .nt 命名一个 Turtle 文件，或者使用 .ttl 命名一个 N-Triples 文件，该工具将不能正确解析此格式。

SPARQL 查询的最简单形式尝试匹配图形的各部分，并选择一个或多个表达为图形模式的重要位置的变量。此方法类似于 SQL SELECT 查询和预测，但它在图形上而不是在表上操作：

```
SELECT variable-list
WHERE {
  graph pattern
}
```

图形模式使用图形表达一种结构关系，引用了节点和链接节点的圆弧。回想一下在 RDF 中，节点转换为主语实体，连接到属性的圆弧将它们连接到图形中的其他节点。如果想要询问特定节点的问题，您可以在模式的主语位置指定这些节点。如果想要知道一个特定属性的值，可以在模式的谓语位置指定该属性。任何您不想指定的元素都可由一个变量来表示，该变量将映射到该位置存在的任何可能值。如果不指定模式的任何部分，则会实际要求图形中的所有元组扁平化到一个结果集中。结果集的内容取决于您选择了哪些变量。

假设您拥有清单 1 中所示的 Turtle 文件（名为 basic.ttl）。

清单 1. 示例 Turtle 文件
```
<https://w3id.org/people/bsletten>
        a       <http://xmlns.com/foaf/0.1/Person> ;
        <http://xmlns.com/foaf/0.1/birthday> "05-26" ;
        <http://xmlns.com/foaf/0.1/name> "Brian Sletten" .
                 
                 
<https://w3id.org/people/mcarducci>
        a       <http://example.com/ns/Magician> ;
        <http://xmlns.com/foaf/0.1/homepage> <http://trulymagic.com> ;
        <http://xmlns.com/foaf/0.1/name> "Michael Carducci" .
```

basic.ttl 文件包含少量有关两个不同实体的事实。这两个实体的图形目前没有连接，所以 basic.ttl 中的完整数据集有两个不同的根节点。如果我想请求我拥有的所有信息，那么我可以避免指定图形模式的任何特定部分，而选择所有变量。


```
SELECT ?s ?p ?o
WHERE {
  ?s ?p ?o
}
```
对我的数据文件运行前面的查询，会得到清单 2 中所示的结果：

清单 2. 查询 basic.ttl 中的所有信息的结果

```
> sparql --query query.rq --data basic.ttl
  --------------------------------------------------------------------------------------------------------------------------------
  | s                                   | p                                                 | o                                  |
  ================================================================================================================================
  | <https://w3id.org/people/mcarducci> | <http://xmlns.com/foaf/0.1/name>                  | "Michael Carducci"                 |
  | <https://w3id.org/people/mcarducci> | <http://xmlns.com/foaf/0.1/homepage>              | <http://trulymagic.com>       |
  | <https://w3id.org/people/mcarducci> | <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> | <http://example.com/ns/Magician>   |
  | <https://w3id.org/people/bsletten>  | <http://xmlns.com/foaf/0.1/name>                  | "Brian Sletten"                    |
  | <https://w3id.org/people/bsletten>  | <http://xmlns.com/foaf/0.1/birthday>              | "05-26"                            |
  | <https://w3id.org/people/bsletten>  | <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> | <http://xmlns.com/foaf/0.1/Person> |
  --------------------------------------------------------------------------------------------------------------------------------
```

在清单 2 的 SPARQL 结果集中，每个选定的变量都有一列，每个匹配的元组都有一行。我选择了 3 个变量 (?s ?p ?o)，所以清单 2 有 3 列。因为我没有为任何主语、谓语或宾语指定值，图形中的每个事实有一行。该数据集在两个图形中有 3 个元组，所以结果有 6 行。

基本上讲，此查询要求 SPARQL 处理器 “告诉我一切信息” — 不是一个对大型数据集运行的友好的查询。了解 LIMIT 子句，您将避免执行令人讨厌的查询（具体而言，可通过 SPARQL 协议在其他人的系统上个执行查询）。

定性地讲，我将获取一个名为 Michael Carducci 的魔术师的姓名和主页，以及一个名为 Brian Sletten 的人的姓名和生日。通过 上一篇文章，您了解了这些关系的含义。但是，如果您无法识别结果集中的一个实体或关系，因为这些实体或关系（在理想情况下）是可解析的 URI，所以您可以简单地对该 URI 发出一个 GET 请求，进一步了解它们引用的对象或它们的含义。

> “对不同数据集运行同一个查询将会扁平化所有元组，无论提到了哪些主语以及存在哪些与它们相关的信息。”

对不同数据集运行同一个查询将会扁平化所有元组，无论提到了哪些主语以及存在哪些与它们相关的信息。这具有重要意义。RDF 为您提供了可移植的数据。SPARQL 可为您提供可移植的查询（但这没有必要）。您不需要理解特定于领域的关系或类型，即可获取该数据和询问相关问题。

## 找到所有主语或所有关系
与请求所有信息相比，询问什么问题可能更有用？可否查找数据集中讨论了哪些主语？在这种情况下，您不关心各种关系或值；您只想要在任何图形中充当着主语的全面的节点列表。图形模式不会更改（因为您仍希望知道任何充当主语的实体），但您仅选择 subject 变量来将图形中的值映射到结果集中。查询变成：

```
SELECT ?s
WHERE {
  ?s ?p ?o
}
```

运行该查询会得到清单 3 中所示的（可能不符合预期的）结果。

清单 3. 仅选择 subject 变量所得到的结果集
```
> sparql --query subjects.rq --data basic.ttl
---------------------------------------
| s                                   |
=======================================
| <https://w3id.org/people/mcarducci> |
| <https://w3id.org/people/mcarducci> |
| <https://w3id.org/people/mcarducci> |
| <https://w3id.org/people/bsletten>  |
| <https://w3id.org/people/bsletten>  |
| <https://w3id.org/people/bsletten>  |
---------------------------------------
```
该数据仅包含两个主语，但对于每个匹配的图形模式，清单 3 中的结果都包含其主语的一个副本。因为已知每个主语的 3 个（不同）方面，所以每个主语会在结果中获得 3 个引用。为了避免此冗余性，可以使用 DISTINCT 关键词来为结果集中的每行请求一个不同的值。通过请求单个变量，您仅获得对图形中的每个惟一主语的一个引用。查询变成：

```
SELECT DISTINCT ?s
WHERE {
  ?s ?p ?o
}
```
（预期的）结果为：

```
> sparql --query distinct-subjects.rq --data basic.ttl 
---------------------------------------
| s                                   |
=======================================
| <https://w3id.org/people/mcarducci> |
| <https://w3id.org/people/bsletten>  |
---------------------------------------
```
如果您对图形中表达的关系而不是主语更感兴趣，您可以选择不同的变量，在本例中为 ?p 变量。因为 ?p 提供图形中将主语 (?s) 连接到宾语 (?o) 的所有位置，所以 ?p 表示图形中的所有谓语关系。在这里，我还会主动将结果限制到不同的值：

```
SELECT DISTINCT ?p 
WHERE 
{ 
  ?s ?p ?o 
}
```
查询结果显示了图形中使用的属性：

```
> sparql --query predicates.rq --data basic.ttl 
-----------------------------------------------------
| p                                                 |
=====================================================
| <http://xmlns.com/foaf/0.1/name>                  |
| <http://xmlns.com/foaf/0.1/homepage>              |
| <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> |
| <http://xmlns.com/foaf/0.1/birthday>              |
-----------------------------------------------------
```

结果表明该数据使用了 FOAF 的 name、homepage 和 birthday 属性和 RDF 的 type 属性，不应对此感到奇怪。您还可以选择不同的宾语，以查找图形中的所有元组中显示的各个值。我会让您尝试自行编写该查询并分析结果。

指定图形模式中的一个主语
现在假设您在对数据运行一个主语查询，而且知道所描述的实体的身份。如果您想要询问一个特定主语的信息，可以指定图形模式中的该值，并选择与该主语关联的所有谓语-宾语对。在这里您可以说，“告诉我您知道某个特定资源的所有信息。”要找到 Michael Carducci 资源的更多信息，查询将类似于：

```
SELECT ?p ?o 
WHERE 
{ 
  <https://w3id.org/people/mcarducci> ?p ?o 
}
```
结果将类似于：

```
> sparql --query carducci.rq --data basic.ttl 
----------------------------------------------------------------------------------------
| p                                                 | o                                |
========================================================================================
| <http://xmlns.com/foaf/0.1/name>                  | "Michael Carducci"               |
| <http://xmlns.com/foaf/0.1/homepage>              | <http://trulymagic.com>          |
| <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> | <http://example.com/ns/Magician> |
----------------------------------------------------------------------------------------
```
这种请求某个特定资源的信息的想法非常有用，以至于另一种 SPARQL 查询格式（称为 DESCRIBE）提供了一种替代方法：

``
DESCRIBE <https://w3id.org/people/mcarducci>
```

您需要知道的是，DESCRIBE 不会像 SELECT 查询一样生成结果集表。相反，DESCRIBE 生成一个新图形，如清单 4 所示。

清单 4. DESCRIBE 生成的图形
```
> sparql --query describe.rq --data basic.ttl 
<https://w3id.org/people/mcarducci>
        a       <http://example.com/ns/Magician> ;
        <http://xmlns.com/foaf/0.1/homepage>
                <http://trulymagic.com> ;
        <http://xmlns.com/foaf/0.1/name>
                "Michael Carducci" .
```

## 找到一个主语标识符

现在假设您的数据集拥有许多人的信息，而且您无法在所有标识符的列表中识别出 Michael 的主语标识符。您可以更改查询来寻找 Michael 的主语标识符。为了简便起见，我还将借此机会介绍使用 PREFIX 来简化图形模式的表达。如果使用 PREFIX 关键字来表明 foaf: 前缀指向 http://xmlns.com/foaf/0.1/ 命令空间，那么可以引用更简单的 foaf:name 或 foaf:Person: 术语：

```
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
SELECT ?s
WHERE 
{ 
  ?s foaf:name "Michael Carducci" 
}
```
结果与您预期的一样。在知道要使用哪个标识符后，您可以运行之前的查询来获取 Michael 的信息。或者可以组合这两步，运行一个更复杂的查询：

1
2
3
4
5
6
7
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
SELECT ?p ?o
WHERE 
{ 
  ?s foaf:name "Michael Carducci";
    ?p ?o .
}
在这里，您可以说，“告诉我您知道的所有名叫 Michael Carducci 的人的所有信息。”如果有多个有关名叫 Michael Carducci 的人的资源，您可以获得所有这些人的结果。因为姓名不是惟一标识符，所以现在您不一定知道这些资源是否指的是同一个人。

前面的查询没有选择主语，但或许它不需要这么做。如果您希望知道该数据中有关名叫 Michael Carducci 的人的信息，资源标识符可能没有用。如果还想捕获该标识符，可以将 ?s 变量添加到 SELECT 子句中。但即使您没有选择该标识符，仍然需要 ?s 在图形模式中发挥其作用。一个主语必须通过 foaf:name 属性连接到您在寻找的姓名，这样才能捕获与该主语关联的属性和宾语值。

最后，您可能还希望可以在更加间接的查询中使用 DESCRIBE 格式。完全可以。在这里，我请求有关 ex:Magician 的任何资源的信息：

1
2
3
4
5
6
PREFIX ex: <http://example.com/ns/>
 
DESCRIBE ?s
WHERE {
  ?s a ex:Magician
}
我在上一个例子中介绍了另一个有用的诀窍，那就是依靠 a 语法糖来避免完整地指定 RDF type 属性。这里的另一个好处是，您可以利用您对该领域的理解来对该数据询问针对性的问题：为我找到某个是 Engineer 的人。为我找到任何具有此姓名的 Person。为我找到一位居住在休斯顿的 Magician。

如果您不知道使用了哪些领域类型，您可以发出另一个查询来获取它们。可以如何请求所有类型的列表？（提示：基本查询是 “向我显示包含一种类型的所有类型。”您希望指定谓语（或使用一种语法糖捷径），但不使用主语或宾语。

查询多个数据集
如果我出于某个荒唐的原因而不能更改我的 basic.ttl 文件，我仍可以扩展其中的事实，方法是将这些事实放在一个不同的文件中并将该文件添加到命令行。假设我希望捕获我所知道的 Michael 的信息。在一个不同的文件 (knows.ttl) 中，我可以说：

1
2
3
4
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
 
<https://w3id.org/people/bsletten>
  foaf:knows <https://w3id.org/people/mcarducci> .
我在使用相同的标识符，所以如果我混合来自此文件的数据和我已使用的数据，我会连接这两个图形。原始文件原封不动，但这个添加到用于查询的模型中的额外元组，会通过 foaf:knows 关系将 Brian 的节点连接到 Michael 的节点。我可以询问的查询变得更加有趣，如清单 5 所示。

清单 5. 查询链接的图形模式
1
2
3
4
5
6
7
8
9
10
11
PREFIX ex: <http://example.com/ns/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
 
SELECT ?magician ?name
WHERE {
  ?s foaf:name "Brian Sletten" ;
     foaf:knows ?magician .
 
  ?magician a ex:Magician ;
     foaf:name ?name .
}
在清单 5 中，我有两个由 magician 变量链接的不同的图形模式。我会说，“告诉我任何 (?s) 名叫 Brian Sletten 的人，这个人需要知道某个是 ex:Magician 类的实例 (?magician) 的人。另外，获取另一个人的姓名。”没有来自 knows.ttl 文件的元组，此查询就无法返回任何结果。但是，如果我将它包含在我的运行时模型中，就会出现奇：

1
2
3
4
5
6
> sparql --query complex.rq --data basic.ttl --data knows.ttl
------------------------------------------------------------
| magician                            | name               |
============================================================
| <https://w3id.org/people/mcarducci> | "Michael Carducci" |
------------------------------------------------------------
查询远程数据
您刚看到了积累来自各种文件的数据的好处，这也适用于可进行 Web 寻址的资源。但是，在这里我会给您出一个难题。还记得在 上一篇文章 中，我使用了一个来自 W3ID 社区的标识符来引用我自己吗？如果您请求该资源，像清单 6 中一样，将会发生什么？

清单 6. 请求 W3ID 资源的查询
1
2
3
4
5
6
7
8
9
10
> http get https://w3id.org/people/bsletten
HTTP/1.1 303 See Other
Access-Control-Allow-Origin: *
Content-Length: 314
Content-Type: text/html; charset=iso-8859-1
Date: Tue, 31 Mar 2015 22:33:15 GMT
Location: http://bosatsu.net/foaf/brian.rdf
Server: Apache/2.4.7 (Ubuntu)
 
...
我在清单 6 中省略了 HTML 正文，因为它的含义从标头中很容易明白。303 See Other 响应包含一个 Location 标头，它指向一个将包含我的更多信息的文件。我无法序列化，但文档可以。当我请求该文档时，如清单 7 中一样，您会看到一些有趣的事情。

清单 7. 请求 W3ID 资源的查询
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
> http get http://bosatsu.net/foaf/brian.rdf
 
HTTP/1.1 200 OK
Accept-Ranges: bytes
Access-Control-Allow-Origin: *
Content-Length: 8507
Content-Type: application/rdf+xml
Date: Tue, 31 Mar 2015 22:40:04 GMT
ETag: "402ab-213b-508583ad90a40"
Last-Modified: Fri, 21 Nov 2014 06:05:21 GMT
Server: Apache/2.2.16 (Debian)
 
<?xml version="1.0" ?>
<rdf:RDF xmlns:cert="http://www.w3.org/ns/auth/cert#"
   xmlns:contact="http://www.w3.org/2000/10/swap/pim/contact#"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:foaf="http://xmlns.com/foaf/0.1/"
   xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
   xmlns:loc="http://simile.mit.edu/2005/05/ontologies/location#"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
   xmlns:wot="http://xmlns.com/wot/0.1/">
 
  <foaf:Person rdf:about="https://w3id.org/people/bsletten">
    ...
  </foaf:Person>
  ...
</rdf:RDF>
RDF 生态系统
SPARQL 查询是一种查询完全不同的数据的强大而又有效的方式，尤其在使用扩大了 RDF 数据范围的支持性技术时。

RDFa 可用于从面向 XML 和 HTML 的文档提取 RDF。
D2RQ 等工具或支持 R2RML 标准的工具可包装 RDBMS 数据库。
您可以使用 JSON-LD 将 JSON API 链接到语义 Web 标准。
来自 CSV on the Web WG 的新兴工具支持将电子表格转换为 RDF。
原生的元组库（例如 Stardog、Virtuoso 和 AllegroGraph）可直接处理 RDF。
网络上发布的文件始终可以存储为 RDF 序列化格式。
在清单 7 中，在 HTTP 响应标头、RDF 文档的开头和所有命名空间前缀后，我将一个名为 https://w3id.org/people/bsletten 的资源识别为 foaf:Person 类的一个实例。（这是 RDF/XML 表格一个主要类型的 rdf:type 关系的方式。）随后是有关这个人（我）的一些细节，包括我在何处上的学，我知道谁，我的兴趣爱好等。

但是，不要忘这种思路的更大背景。我为自己提供的稳定的标识符 303-重定向到一个描述我的文档（使用同一个标识符）。一个理解 Web 重定向的 SPARQL 客户端（它们大部分都理解）将解决所有这些问题。所以，在命令行上添加一个对我的引用，也会添加（在跟随重定向后）我公开宣传的有关我自己的所有信息。现在您可连接 3 个不同的数据集并询问问题：“告诉我任何知道一位名叫 Michael Carducci 的魔术师的人的兴趣。”这些兴趣来自这个新重定向的 RDF 文件。这个新查询类似于清单 8。

清单 8. 链接 3 个资源的查询
1
2
3
4
5
6
7
8
9
10
PREFIX ex: <http://example.com/ns/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
 
SELECT ?interest WHERE {
  ?s foaf:interest ?interest ;
     foaf:knows ?magician .
    
  ?magician a ex:Magician ;
     foaf:name "Michael Carducci" .
}
清单 9 显示了结果。

清单 9. 来自清单 8 的查询的结果
1
2
3
4
5
6
7
8
9
10
11
> sparql --query interests.rq --data basic.ttl --data knows.ttl --data https://w3id.org/people/bsletten
-----------------------------------------------
| interest                                    |
===============================================
| <http://dbpedia.org/page/Fish_%28singer%29> |
| <http://dbpedia.org/resource/Sushi>         |
| <http://dbpedia.org/resource/Phish>         |
| <http://www.w3.org/2000/01/sw/>             |
| <http://www.w3.org/Metadata/>               |
| <http://www.w3.org/RDF/>                    |
-----------------------------------------------
“您可询问的查询的复杂性与您拥有的数据的复杂性直接相关。”

发生了许多事情。basic.ttl 中的元组建立了一些有关 Michael 和我的简单事实。来自 knows.ttl 文件的单一事实捕获了我知道的 Michael 的事实，并将 basic.ttl 文件中的两个（平时未连接的）图形连接在一起。最后，我在支持我的 W3ID 标识符的 FOAF 文件中发布的数据表达了我的一些兴趣（以及其他趣闻）。清单 8 中的查询之所有有效，离不开从所有这 3 种来源提供的链接。您可询问的查询的复杂性与您拥有的数据的复杂性直接相关。请记住，您可获取数据，然后向它询问它所提及的内容，涉及的属性等。

SPARQL 协议
当 SPARQL 客户端解析和运行查询时，它首先通过 HTTP 抓取数据，并将这些数据拉入到引擎中。这很有趣，因为数据始终是新鲜的，是从来源拉取的。如果来源数据更改，下一次您运行查询时，就会获得更新。但是，对于大型数据集，每次运行查询时拉取的信息量可能无法控制，尤其在您仅需要结果图形的一小部分时。为了帮助解决这个问题，SPARQL 协议 允许您对任何支持一些少量约定的远程服务器运行查询。这里仅重点介绍一种服务器。

如果服务器支持使用 GET 模式来运行 SPARQL 查询，它将接受作为参数传递的查询的 URL 编码格式。这是我在 我的面向资源的模式图书 中所称的命名查询模式 (Named Query Pattern) 的一个例子。客户端通过只请求它感兴趣的信息，为这些信息创建一个信息资源（而不是由服务器发布一个指定的资源）。获取查询：

1
2
3
4
5
6
PREFIX ex: <http://example.com/ns/>
 
DESCRIBE ?s
WHERE {
  ?s a ex:Magician
}
前面的查询将转换为一个对 http://example.com/sparql?query=PREFIX%20ex%3A%20%26lt%3Bhttp%3A%2F%2Fexample.com%2Fns%2F%26gt%3B%0A%0ADESCRIBE%20%3Fs%0AWHERE%20%7B%0A%20%20%3Fs%20a%20ex%3AMagician%0A%7D 的请求，您可以向该资源发出一个 GET 请求。结果集通常可序列化为 SPARQL 结果集格式的 XML、JSON 或 CSV 表示。但是，查询的仍是一个信息资源，可以共享、加入书签，甚至缓存（例如，如果服务器指定可使用缓存控件）。

考虑这里实际发生的事情。无需为我拉入数据，我将查询推送到数据所在的服务器。此功能可为大型数据集节省大量带宽。但是，这种节省并不是有利无害。允许任意客户端推动在您服务器上执行的任意查询，可能风险太大。幸运的是，这些客户端驱动的资源仍然只是信息资源，可由您想要部署的任何身份验证和授权系统来保护。（一个叫做 Linked Data Fragments 的计划正在努力定义标准，以允许客户发出轻量型查询来实现与使用 SPARQL 协议相同的结果。）

将协议支持与 SPARQL SELECT 查询相结合仍然具有强大的威力。或许这种威力可通过结合使用 CONSTRUCT 格式和该协议来更具体地体现出来。借助 CONSTRUCT，您可以根据现有图形创建一个新图形。这些新图形可能是自图形，一个特定资源（比如 DESCRIBE 查询）的图形，或者一个以您想要的方式形成的图形。

我最后的例子将展示如何以最少的努力释放前所未有的威力。如果您为资源标识符、属性、类等使用与我不同的术语，我可以使用我的术语为您的数据创建一个子图形，方法是请求该数据并将它推送到您的 SPARQL 协议端点。不需要无止境地举行徒劳的会议来试图达成一致。如果您将某个东西称为 ex:author 来表示作者，并且我希望使用更广泛使用的 Dublin Core http://purl.org/dc/terms/creator，我可输入以下查询：

1
2
3
4
5
6
7
8
PREFIX ex: <http://example.com/ns/>
PREFIX dct: <http://purl.org/dc/terms/>
 
CONSTRUCT {
  ?x dct:creator ?y .
} WHERE {
  ?x ex:author ?y .
}
然后通过 http://example.com/sparql?query=PREFIX%20ex%3A%20%3Chttp%3A%2F%2Fexample.com%2Fns%2F%3E%0APREFIX%20dct%3A%20%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0A%0ACONSTRUCT%20%7B%0A%20%20%3Fx%20dct%3Acreator%20%3Fy%20.%0A%7D%20WHERE%20%7B%0A%20%20%3Fx%20ex%3Aauthor%20%3Fy%20.%0A%7D 将该查询推送到您的服务器。请记住，CONSTRUCT 查询（比如 DESCRIBE 查询）生成的是 RDF 图形，而不是结果集。所以，我随后可以直接在客户端上查询 SPARQL 协议资源，如清单 10 所示。

清单 10. 直接从客户端查询 SPARQL 协议资源
1
2
3
4
5
6
7
8
PREFIX dct: <http://purl.org/dc/terms/>
 
SELECT ?creator
FROM <http://example.com/sparql?query=PREFIX%20ex%3A%20%3Chttp%3A%2F%2Fexample.com%2Fns%2F%3E%0APREFIX%20dct%3A%20%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0A%0ACONSTRUCT%20%7B%0A%20%20%3Fx%20dct%3Acreator%20%3Fy%20.%0A%7D%20WHERE%20%7B%0A%20%20%3Fx%20ex%3Aauthor%20%3Fy%20.%0A%7D>
FROM some local file or data
WHERE {
  ?x dct:creator ?creator .
}
我通过网络动态地获取您的数据，并以我想要看到它的形式来对您的数据进行整形，而不特别关注数据最终是如何生成或存储的（它可以是一个 RDBMS），然后将它连接到我的数据，而不一定需要与您协调（除了获取对 SPARQL 协议端点的访问权）。

在下一期讨论 Linked Data 时，您会看到使用 SPARQL 协议的一些有趣示例。

结束语
在本系列中，目前为止，您了解了一种灵活的标准数据模型，它使您始终能获得新事实，并以极少的工作轻松地与新数据来源集成。您现在还看到了如何跨数据来源运行查询，而无需考虑底层实现细节 — 通过使用标准标识符、标准模型和一种标准查询语言为通过网络询问任意领域的任意问题。在下一期文章中，将在这些想法的基础上，探索 Linked Data 和 Linked Data Platform。