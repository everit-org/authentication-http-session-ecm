/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.authentication.http.session.ecm.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.eclipse.jetty.server.Server;
import org.everit.authentication.context.AuthenticationContext;
import org.everit.authentication.http.session.ecm.SessionAuthenticationConstants;
import org.everit.osgi.dev.testrunner.TestDuringDevelopment;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Test for Session Authentication Component.
 */
@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE,
        defaultValue = "junit4"),
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID,
        defaultValue = "SessionAuthenticationTestComponent") })
@Service(value = SessionAuthenticationComponentTest.class)
public class SessionAuthenticationComponentTest {

  private static final String HELLO_SERVLET_ALIAS = "/hello";

  private static final String LOGOUT_SERVLET_ALIAS = "/logout-action";

  private static final int RESPONSE_BODY_LENGTH = 3;

  private AuthenticationContext authenticationContext;

  private String helloUrl;

  private String loggedOutUrl;

  private String logoutUrl;

  private Server server;

  /**
   * Component activator method.
   */
  @Activate
  public void activate(final BundleContext context, final Map<String, Object> componentProperties)
      throws Exception {
    String testServerURI = server.getURI().toString();
    String testServerURL = testServerURI.substring(0, testServerURI.length() - 1);

    helloUrl = testServerURL + HELLO_SERVLET_ALIAS;
    logoutUrl = testServerURL + LOGOUT_SERVLET_ALIAS;
    loggedOutUrl = testServerURL + SessionAuthenticationConstants.DEFAULT_LOGGED_OUT_URL;
  }

  private long hello(final HttpContext httpContext, final long expectedResourceId)
      throws IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(helloUrl);
    HttpResponse httpResponse = httpClient.execute(httpGet, httpContext);
    Assert.assertEquals(HttpServletResponse.SC_OK, httpResponse.getStatusLine().getStatusCode());
    HttpEntity responseEntity = httpResponse.getEntity();
    InputStream inputStream = responseEntity.getContent();
    StringWriter writer = new StringWriter();
    IOUtils.copy(inputStream, writer);
    String[] responseBodyAsString = writer.toString().split(":");
    long actualResourceId = Long.parseLong(responseBodyAsString[0]);
    long newResourceId = Long.parseLong(responseBodyAsString[1]);
    String st = responseBodyAsString.length == RESPONSE_BODY_LENGTH
        ? responseBodyAsString[2]
        : "should be success";
    Assert.assertEquals(st.replaceAll("-->", ":"), expectedResourceId, actualResourceId);
    return newResourceId;
  }

  private void logoutGet(final HttpContext httpContext)
      throws ClientProtocolException, IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(logoutUrl);
    HttpResponse httpResponse = httpClient.execute(httpGet, httpContext);
    Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, httpResponse.getStatusLine()
        .getStatusCode());

    HttpUriRequest currentReq = (HttpUriRequest) httpContext
        .getAttribute(ExecutionContext.HTTP_REQUEST);
    HttpHost currentHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
    String currentUrl = (currentReq.getURI().isAbsolute())
        ? currentReq.getURI().toString()
        : (currentHost.toURI() + currentReq.getURI());
    Assert.assertEquals(loggedOutUrl, currentUrl);
  }

  private void logoutPost(final HttpContext httpContext) throws ClientProtocolException,
      IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(logoutUrl);
    HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
    Assert.assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, httpResponse.getStatusLine()
        .getStatusCode());
    Header locationHeader = httpResponse.getFirstHeader("Location");
    Assert.assertEquals(loggedOutUrl, locationHeader.getValue());
  }

  @ServiceRef(defaultValue = "")
  public void setAuthenticationContext(final AuthenticationContext authenticationContext) {
    this.authenticationContext = authenticationContext;
  }

  @ServiceRef(defaultValue = "")
  public void setServer(final Server server) {
    this.server = server;
  }

  @Test
  @TestDuringDevelopment
  public void testAccessHelloPage() throws Exception {
    CookieStore cookieStore = new BasicCookieStore();
    HttpContext httpContext = new BasicHttpContext();
    httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    long sessionResourceId = hello(httpContext, authenticationContext.getDefaultResourceId());
    sessionResourceId = hello(httpContext, sessionResourceId);
    sessionResourceId = hello(httpContext, sessionResourceId);
    logoutPost(httpContext);

    sessionResourceId = hello(httpContext, authenticationContext.getDefaultResourceId());
    sessionResourceId = hello(httpContext, sessionResourceId);
    hello(httpContext, sessionResourceId);
    logoutGet(httpContext);

    hello(httpContext, authenticationContext.getDefaultResourceId());
  }

}
