package utils;
import base.Base;
import constants.Constants;
import pojo.ResponseResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpUtil {
	public static Logger logger=Logger.getLogger(HttpUtil.class);

	/**
	 * 无参数的get请求
	 * 
	 * @param uri 接口地址
	 */
	public static ResponseResult doGet(String uri, boolean isAuthorization) {
		CloseableHttpResponse response=null;
		// 创建客户端
		CloseableHttpClient client = HttpClients.createDefault();
		try {
//		 * 1、创建request连接.
			HttpGet get = new HttpGet(uri);
			// 设置请求头
//			get.setHeader(Constants.HEADER_MEDIA_TYPE_NAME, Constants.HEADER_MEDIA_TYPE_VALUE);
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(get);
			}
			// 创建一个客户端
			 client = HttpClients.createDefault();
			// 发送请求,并接受响应
			 response = client.execute(get);
			// 获取接口响应封装到一个方法中，方便调用,返回body
			return getResult(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close(client, response);
		}
		return null;
	}

	/**
	 * 带参数的get请求，参数为map
	 * 
	 * @param uri 接口地址
	 * @param mapParams 接口参数
	 */
	public static ResponseResult doGet(String uri, Map<Object, Object> mapParams, boolean isAuthorization) {
		CloseableHttpResponse response=null;
		// 创建客户端
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			// 在uri上拼接参数2.那么一个完整的url地址，基本格式如下：
			// https://host:port/path?xxx=aaa&ooo=bbb
			Set<Object> keySet = mapParams.keySet();
			int i = 0;
			for (Object key : keySet) {
				//如果map的value值不是字符串，拼接在url中会抛异常，所以先用object类型接收，再转换成string
				Object object=mapParams.get(key);
				if (i == 0) {
//                    java.net.URLEncoder.encode(object.toString(), "utf-8")//解码
                    uri = uri + "?" + key + "=" + java.net.URLEncoder.encode(object.toString(), "utf-8");
				} else {
					uri = uri + "&" + key + "=" + java.net.URLEncoder.encode(object.toString(), "utf-8");
				}
				i++;
			}
			// 创建一个get请求
			HttpGet get = new HttpGet(uri);
			// 设置请求头
//			get.addHeader(Constants.HEADER_MEDIA_TYPE_NAME, Constants.HEADER_MEDIA_TYPE_VALUE);
			get.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_FORM);
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(get);
			}
			// 创建一个客户端
			 client = HttpClients.createDefault();
			// 客户端发送请求，接受响应
			 response = client.execute(get);
			return getResult(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close(client, response);
		}
		return null;
	}

	public static ResponseResult doDelete(String uri, String params, boolean isAuthorization) {
		CloseableHttpResponse response=null;
		// 创建客户端
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			if(params!=null&&!"".equals(params)) {
				uri = uri + "/" + params;
			}
			HttpDelete delete = new HttpDelete(uri);
			// 设置请求头
//			get.addHeader(Constants.HEADER_MEDIA_TYPE_NAME, Constants.HEADER_MEDIA_TYPE_VALUE);
			delete.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(delete);
			}
			// 创建一个客户端
			 client = HttpClients.createDefault();
			// 客户端发送请求，接受响应
			 response = client.execute(delete);
			return getResult(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close(client,response);
		}
		return null;
	}

	/**
	 * get请求，参数为 key=value&key=value 格式 或者是json字符串
	 * 
	 * @param
	 * @param
	 */
	public static void main(String[] args) {
		String params="{\"name\":\"#\"}";
		HashMap<Object, Object> mapParams = new HashMap<Object, Object>();
		mapParams = JSONObject.parseObject(params, HashMap.class);
		System.out.println(mapParams);
	}
	public static ResponseResult doGet(String uri, String params, boolean isAuthorization) {
		if (params.contains("?")) {
			uri = uri + "?" + params;
			return doGet(uri, isAuthorization);
		} else {// 如果参数为json格式
			HashMap<Object, Object> mapParams = new HashMap<Object, Object>();
			try {// json字符串转成map
				mapParams = JSONObject.parseObject(params, HashMap.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return doGet(uri, mapParams, isAuthorization);
		}
	}
	/**
	 * get请求，接口地址中包含参数
	 * @param uri
	 * @param params
	 * @param isAuthorization
	 * @return
	 */

/*	private static ResponseResult doGetUrlHaveParams(String uri, String params, boolean isAuthorization) {
		//提取出参数名
		String paramName=uri.substring(uri.indexOf("{")+1, uri.indexOf("}"));
		String paramValue="";
		try {
			//提取出参数值
			if(params != null && !params.equals("")) {
				paramValue=JsonPath.read(params, "$."+paramName);
			}
		}catch (Exception e){
			e.printStackTrace();
			logger.error("jsonpath解析失败");
		}
		//替换url
		uri=uri.replace("{"+paramName+"}", paramValue);
		//发起接口请求
		return doGet(uri, isAuthorization);
	}*/

	/**
	 * post提交数据方式：application/x-www-form-urlencoded
	 * 
	 * @param uri
	 * @param params          为json格式
	 * @param isAuthorization
	 */
//	public static ResponseResult doFormPost1(String uri, String params, boolean isAuthorization) {
//		try {
//			// 创建post请求
//			HttpPost post = new HttpPost(uri);
//
//			// 添加请求头
//			// 添加请求头
////			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
////			post.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_FORM);
//			post.addHeader(Constants.CONTENT_TYPE,Constants.CONTENT_TYPE_FORMDATA);
//
//			// 如果需要鉴权，添加请求头
//			if (isAuthorization == true) {
//				AuthorizationUtils.setTokenInRequest(post);
//			}
//			// 将json字符串转换成key-value的格式
//			if (params.contains("{")) {
//				params = json2keyValue(params);
////				System.out.println(params);
//			}
//
////		System.out.println(params);
//			// 设置body
//			post.setEntity(new StringEntity(params));
//
//			// 创建客户端
//			HttpClient client = HttpClients.createDefault();
//			// 发送请求
//			HttpResponse response = client.execute(post);
//			return getResult(response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Additional methods form表单提交数据，读取文件流
	 * @author Yoyo&Xiuxiu
	 * @param uri
	 * @param params
	 * @param isAuthorization
	 * @return
	 */
	public static ResponseResult doFormPost(String uri, String params, boolean isAuthorization) {
		// 创建客户端
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response=null;
		try {
			// 创建post请求
			HttpPost post = new HttpPost(uri);

			// 添加请求头
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

			// 如果需要鉴权，添加请求头
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(post);
			}
			if (!StringUtils.isEmpty(params)) {
				Map<String,String> jsonObject = JSONObject.parseObject(params,Map.class);//转成map
				for (String key : jsonObject.keySet()) {
						String value = jsonObject.get(key);

					// 匹配resource下的zip 文件
					if(value.contains("zip")||"file".equals(key)){

						File file =new File(value);
						if(file.exists()){
							// 把文件转换成流对象FileBody
							FileBody binFileBody=new FileBody(file);
							multipartEntityBuilder.addPart(key,binFileBody);
						}else{
							multipartEntityBuilder.addPart(key, new StringBody(value, ContentType.create("text/plain", Consts.UTF_8)));
						}
					}else{
						multipartEntityBuilder.addPart(key, new StringBody(value, ContentType.create("text/plain", Consts.UTF_8)));
                        }
				}
			}

			//这个才是解决中文乱码的终极方法
			HttpEntity entity=multipartEntityBuilder.setMode(HttpMultipartMode.RFC6532).build();
			post.setEntity(entity);

			 response = client.execute(post);
			EntityUtils.consume(entity);  // 销毁
			return getResult(response);

		} catch (Exception e) {
			e.printStackTrace();

		}finally {
			close(client, response);
		}
		return null;
	}

	private static void close(CloseableHttpClient client, CloseableHttpResponse response) {
		closeClient(client);
		closeResponse(response);
	}

	private static void closeResponse(CloseableHttpResponse response) {
		try {
			if(response!=null){

				response.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void closeClient(CloseableHttpClient client) {
		try {
			if(client!=null){

				client.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/*	*//**
	 * base64转io流
 	 *//*
	private static InputStream baseToInputStream(String base64string){
		ByteArrayInputStream stream = null;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] bytes1 = decoder.decodeBuffer(base64string);
			stream = new ByteArrayInputStream(bytes1);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
		}
		return stream;
	}*/



	/**
	 * json字符串转换成key-value
	 * 
	 * @param params
	 * @return
	 */
	public static String json2keyValue(String params) {
		String result = "";
		try {
			// fson字符串转换成map，再组装

			Map<Object, Object> map = JSON.parseObject(params, Map.class);
			Set<Object> keySet = map.keySet();
			for (Object key : keySet) {
				String value = (String) map.get(key);
				if (result.length() > 0) {
					result += "&";
				}
				result += key + "=" + value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * post提交数据方式：application/json
	 * 
	 * @param uri
	 * @param params          application/json
	 * @param isAuthorization 是否需要鉴权
	 */
	public static ResponseResult doPost(String uri, String params, boolean isAuthorization) {

		try {
			// 创建post请求,用父类接受子类
			HttpPost post = new HttpPost(uri);
			// 添加请求头
//			post.addHeader(Constants.HEADER_MEDIA_TYPE_NAME, Constants.HEADER_MEDIA_TYPE_VALUE);
			post.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(post);
			}
//			logger.info(params);
			// 设置body
			post.setEntity(new StringEntity(params, "utf-8"));
//			logger.info("params");
			// 创建客户端
			HttpClient client = HttpClients.createDefault();
			// 发送请求
			HttpResponse response = client.execute(post);
			return getResult(response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * post提交数据方式：multipart/form-data
	 * @Author: Yoyo
	 * @param uri
	 * @param params          multipart/form-data
	 * @param isAuthorization 是否需要鉴权
	 */
	public static ResponseResult doPostFormData(String uri, String params, boolean isAuthorization) {
		try {
//			rui=pathparamte +uri

			// 创建post请求,用父类接受子类
			HttpPost post = new HttpPost(uri);
			// 添加请求头
//			post.addHeader(Constants.HEADER_MEDIA_TYPE_NAME, Constants.HEADER_MEDIA_TYPE_VALUE);
			post.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_FORMDATA);
			if ( isAuthorization== true) {
				AuthorizationUtils.setTokenInRequest(post);
			}
			// 设置body
			post.setEntity(new StringEntity(params, "utf-8"));
			// 创建客户端
			HttpClient client = HttpClients.createDefault();
			// 发送请求
			HttpResponse response = client.execute(post);
			return getResult(response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 默认不需要鉴权
	 * 
	 * @param uri    接口地址
	 * @param params 接口参数
	 * @return
	 */
	public static ResponseResult doPost(String uri, String params) {
		return doPost(uri, params, false);
	}

	/**
	 * 参数为map类型的post请求
	 * 
	 * @param uri
	 * @param paramsMap
	 * @return
	 */
	public static ResponseResult doPost(String uri, Map<String, String> paramsMap, boolean isAuthorization) {
		String params = JSON.toJSONString(paramsMap);
		return doPost(uri, params, isAuthorization);

	}

	/**
	 * patch请求
	 * 
	 * @param uri             接口地址
	 * @param params          接口参数
	 * @param isAuthorization 是否需要鉴权
	 * @return
	 */
	public static ResponseResult doPatch(String uri, String params, boolean isAuthorization) {
		// 创建patch请求
		HttpPatch patch = new HttpPatch(uri);
		// 设置header
		patch.setHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
		// 如果需要鉴权
		if (isAuthorization == true) {
			// 请求头添加token
			AuthorizationUtils.setTokenInRequest(patch);
		}
		// 设置参数
		patch.setEntity(new StringEntity(params, "utf-8"));
		// 创建客户端
		HttpClient client = HttpClients.createDefault();
		try {
			HttpResponse response = client.execute(patch);
			return getResult(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取响应结果
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static ResponseResult getResult(HttpResponse response) throws IOException {
		ResponseResult responseResult=new ResponseResult();
		// 获取状态码
		int code = response.getStatusLine().getStatusCode();
		responseResult.setCode(code);
//		System.out.println(code);
		// 获取body
		HttpEntity entity = response.getEntity();
		String body = EntityUtils.toString(entity, "utf-8");
		responseResult.setBody(body);
//		System.out.println(body);
		// 获取头信息
		Header[] allHeaders = response.getAllHeaders();
		String headers = Arrays.toString(allHeaders);
//		System.out.println(headers);

		// 返回一个数组，包含状态码和body
		return responseResult;
	}

	/**
	 * 调用接口请求，默认是否鉴权为 false ，不需要鉴权
	 * 
	 * @param uri         接口地址
	 * @param type        请求方式
	 * @param params      参数
	 * @param contentType 参数请求类型
	 * @return
	 */
//	public static ResponseResult call(String uri, String type, String params, String contentType) {
//		return call(uri, type, params, contentType, false);
//	}


	/**
	 * 调用接口请求
	 *
	 * @param uri         接口地址
	 * @param type        接口类型
	 * @param params      参数
	 * @param contentType 参数请求类型
	 */
	public static ResponseResult call(String uri, String type, String params,String contentType, boolean isAuthorization) {
		try {
			 uri = Base.replaceUrl(uri);
//			 params= Base.replace(params);
			// 如果请求方式为get
			if ("get".equalsIgnoreCase(type)) {
				// 判断参数是否为空，选择不同的请求方法
				if (params != null && params.length() > 0) {
					return doGet(uri, params, isAuthorization);
				} else {
					return doGet(uri, isAuthorization);
				}
			} else if ("post".equalsIgnoreCase(type)) {
				//{"key":"$","key2":"\"}
				if ("application/json".equals(contentType)) {
					// 调用application/json格式的post'请求
					return doPost(uri, params, isAuthorization);
				} else if ("form".equals(contentType) || "multipart/form-data".equals(contentType)) {
					// 调用form格式的post'请求
					return doFormPost(uri, params, isAuthorization);
				}else{
					System.out.println("content-type 不正确，无法调用接口请求");
				}
			} else if ("PATCH".equalsIgnoreCase(type)) {
				// 调用patch请求
				return doPatch(uri, params, isAuthorization);
			} else if ("put".equalsIgnoreCase(type)) {
				if ("application/json".equals(contentType)) {
					// 调用form格式的put请求
					return doPut(uri, params, isAuthorization);
				} else if ( "multipart/form-data".equals(contentType)) {
					// 调用form格式的put请求
					return doFormPut(uri, params, isAuthorization);
				}else if("application/x-www-form-urlencoded".equals(contentType)){
					//调用x-www-form-urlencoded格式的put请求
					return  doUrlencodedFormPut(uri,params,isAuthorization);
				}else{
					System.out.println("content-type 不正确，无法调用接口请求");
				}
			} else if ("delete".equalsIgnoreCase(type)) {
				return doDelete(uri, params, isAuthorization);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 
	 * @param uri             接口地址
	 * @param params          接口参数
	 * @param isAuthorization
	 * @return
	 */
	private static ResponseResult doPut(String uri, String params, boolean isAuthorization) {
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response=null;

		try {

			// 创建post请求,用父类接受子类
			HttpPut put = new HttpPut(uri);
			// 添加请求头
//			post.addHeader(Constants.HEADER_MEDIA_TYPE_NAME, Constants.HEADER_MEDIA_TYPE_VALUE);
			put.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(put);
			}
			// 设置body
			logger.info(params);
			put.setEntity(new StringEntity(params, "utf-8"));
			logger.info(params);

			// 创建客户端
			 client = HttpClients.createDefault();
			// 发送请求
			 response = client.execute(put);
			return getResult(response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close(client, response);
		}
		return null;
	}

	/**
	 * @authoe Yoyo
	 * Put方式的form表单提交
	 * @param uri
	 * @param params
	 * @param isAuthorization
	 * @return
	 */

	public static ResponseResult doFormPut(String uri, String params, boolean isAuthorization) {
		CloseableHttpResponse response=null;
		// 创建客户端
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			// 创建post请求
			HttpPut put = new HttpPut(uri);
			// 添加请求头
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            // 如果需要鉴权，添加请求头
            if (isAuthorization == true) {
                AuthorizationUtils.setTokenInRequest(put);
            }
            if (!StringUtils.isEmpty(params)) {
                JSONObject jsonObject = JSONObject.parseObject(params);
                for (String key : jsonObject.keySet()) {
                    String value = jsonObject.getString(key);

                    // 匹配resource下的zip 文件
                    if(value.contains("zip")||"file".equals(key)){
                    	try {
							FileBody bin = new FileBody(new File(jsonObject.getString("file")));
							builder.addPart(key, bin);
						}catch (Exception e){//有可能创建文件失败，捕获异常
                    		logger.error(e);
						}

//                        FileBody binFileBody=new FileBody(new File(value));
//                        builder.addPart(key,binFileBody);
                    }else{
                        builder.addPart(key, new StringBody(value, ContentType.create("text/plain", Consts.UTF_8)));
                    }
                }
            }

            HttpEntity reqEntity = builder.build();
            put.setEntity(reqEntity);
            logger.info(params);

             response = client.execute(put);
			EntityUtils.consume(reqEntity);  // 销毁
            return getResult(response);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	close(client,response);
		}
        return null;
    }


	/**
	 * Put方式的application/x-www-form-urlencoded 接口数据处理
	 * @authoe Yoyo
	 * @param uri
	 * @param params
	 * @param isAuthorization
	 * 使用HttpClient发送请求、接收响应很简单，一般需要如下几步即可。
	 * 1. 创建HttpClient对象。
	 * 2. 创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。
	 * 3. 如果需要发送请求参数，可调用HttpGet、HttpPost共同的setParams(HttpParams params)方法来添加请求参数；对于HttpPost对象而言，也可调用setEntity(HttpEntity entity)方法来设置请求参数。
	 * 4. 调用HttpClient对象的execute(HttpUriRequest request)发送请求，该方法返回一个HttpResponse。
	 * 5. 调用HttpResponse的getAllHeaders()、getHeaders(String name)等方法可获取服务器的响应头；调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
	 * 6. 释放连接。无论执行方法是否成功，都必须释放连接
	 * @return
	 */

	public static ResponseResult doUrlencodedFormPut(String uri, String params, boolean isAuthorization) {
//		String json= JSON.toJSONString(params);
//		JSONObject jsonObject = JSONObject.parseObject(params);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response=null;
		try {
			// 创建put请求
			HttpPut put = new HttpPut(uri);

			// 添加请求头
			put.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_FORM);

			// 如果需要鉴权，添加请求头
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(put);
			}

           //设置body
			put.setEntity(new StringEntity(params, "utf-8"));

			// 创建HttpClient对象
			 client = HttpClients.createDefault();
			 response = client.execute(put);
			return getResult(response);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			close(client,response);
		}
		return null;
	}
	/**
	 * 参数中的值又为json
	 * @param uri
	 * @param params
	 * @param isAuthorization
	 * @return
	 */
	public static ResponseResult doPostjson(String uri, String params, boolean isAuthorization) {
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response=null;
		try {
			// 创建post请求,用父类接受子类
			HttpPost post = new HttpPost(uri);
			// 添加请求头
			post.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
			if (isAuthorization == true) {
				AuthorizationUtils.setTokenInRequest(post);
			}
			Map<String, Object> map = (Map<String, Object>) JSONObject.parse(params);

			// 设置body
			List< NameValuePair> parameters=new ArrayList<NameValuePair>();
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				parameters.add(new BasicNameValuePair(key, map.get(key).toString()));
				
			}
			HttpEntity entity=new UrlEncodedFormEntity(parameters, "utf-8");
			post.setEntity(entity);
			// 创建客户端
			 client = HttpClients.createDefault();
			// 发送请求
			 response = client.execute(post);
			return getResult(response);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			close(client, response);
		}
		return null;
	}


}
