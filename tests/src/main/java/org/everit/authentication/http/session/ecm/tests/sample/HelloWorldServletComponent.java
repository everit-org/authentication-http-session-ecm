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
package org.everit.authentication.http.session.ecm.tests.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.everit.authentication.context.AuthenticationContext;
import org.everit.authentication.http.session.AuthenticationSessionAttributeNames;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.web.servlet.HttpServlet;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Servlet for Hello World page.
 */
@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@Service(Servlet.class)
public class HelloWorldServletComponent extends HttpServlet {

  private AuthenticationContext authenticationContext;

  private AuthenticationSessionAttributeNames authenticationSessionAttributeNames;

  private void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    long currentResourceId = authenticationContext.getCurrentResourceId();
    StringBuilder sb = null;
    if (currentResourceId == 1) {
      sb = new StringBuilder();
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      for (StackTraceElement stackTraceElement : stackTrace) {
        sb.append("\tat ").append(stackTraceElement).append("\n");
      }
    }

    HttpSession httpSession = req.getSession();
    Random random = new Random();
    long newResourceId = random.nextLong();
    httpSession.setAttribute(authenticationSessionAttributeNames.authenticatedResourceId(),
        newResourceId);

    resp.setContentType("text/plain");
    PrintWriter out = resp.getWriter();
    out.print(currentResourceId + ":" + newResourceId);
    if (sb != null) {
      out.print(":\n === Server stackrace for analizing Filter chain and Servlet invocations ===\n"
          + sb.toString().replaceAll(":", "-->")
          + " === Server stacktrace END ===\n");
    }
  }

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    doGet(req, resp);
  }

  @ServiceRef(defaultValue = "")
  public void setAuthenticationContext(final AuthenticationContext authenticationContext) {
    this.authenticationContext = authenticationContext;
  }

  @ServiceRef(defaultValue = "")
  public void setAuthenticationSessionAttributeNames(
      final AuthenticationSessionAttributeNames authenticationSessionAttributeNames) {
    this.authenticationSessionAttributeNames = authenticationSessionAttributeNames;
  }

}
