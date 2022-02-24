# openapi-sdk-java

## 概览

[COP](https://github.com/cop-cos/COP) SDK in Java，实现了请求签名的生成和应答签名的验证。

如果你是使用Apache HttpClient的开发者，请参考[openapi-apache-httpclient](https://github.com/cop-cos/openapi-sdk-java/tree/main/openapi-apache-httpclient)。

如果你是使用Okhttp3的开发者，请参考[openapi-okhttp3](https://github.com/cop-cos/openapi-sdk-java/tree/main/openapi-okhttp3)。

## 项目状态

当前版本`0.0.1`为试运营期版本。请COP入驻开发者的专业技术人员在使用时注意系统和软件的正确性和兼容性，以及带来的风险。

## 升级指引

-

## 环境要求

+ Java 1.8+

## 安装

Maven Central - 待发布。
请直接下载Release包。

## 名词解释

+ apiKey和secretKey。对于通过入驻审核的COP开发者，平台会分配一组apiKey和secretKey作为开发者的识别凭证，开发者务必妥善保存apiKey和secretKey，生产正式环境中的apiKey和secretKey将作为COP客户应用的唯一凭证。

## 开始

如果你使用的是Apache HttpClient:

```java
	import com.coscon.cop.core.ClientException;
	import com.coscon.cop.core.CommonResponse;
	import com.coscon.cop.core.Namespace;
	
	import com.coscon.cop.httpclient.CopClient;
	//...
	
	CopClient copClient = CopClient.newInstance();
	Namespace ns = Namespace.COP_PUBLIC_PP;
	// 设置对应的COP环境，以及相应环境所使用的凭据apiKey & secretKey
	copClient.withCredentials(ns, System.getenv("cop.pp.apiKey"),
			System.getenv("cop.pp.secretKey"));
	
	// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClienthttpClientBuilder
	
	// 基于以上设置，构建httpClient
	copClient.buildHttpClient();
	
	HttpResponse response = copClient.doGetWithResponse(ns, "service_relative_uri",headers);
	
```

如果你使用的是Okhttp3:

```java

	import com.coscon.cop.core.ClientException;
	import com.coscon.cop.core.CommonResponse;
	import com.coscon.cop.core.Namespace;
	
	import com.coscon.cop.okhttp.CopClient;
	//...
	
	CopClient copClient = CopClient.newInstance();
	Namespace ns = Namespace.COP_PUBLIC_PP;
	// 设置对应的COP环境，以及相应环境所使用的凭据apiKey & secretKey
	copClient.withCredentials(ns, System.getenv("cop.pp.apiKey"),
			System.getenv("cop.pp.secretKey"));
	
	// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClienthttpClientBuilder
	
	// 基于以上设置，构建httpClient
	copClient.buildHttpClient();
	
	Response response = copClient.doGetWithResponse(ns, "service_relative_uri",headers);
	
```

## 联系我们

如果你发现了**BUG**或者有任何疑问、建议，请通过issue进行反馈。
