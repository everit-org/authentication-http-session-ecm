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
package org.everit.authentication.http.session.ecm.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.everit.authentication.context.AuthenticationPropagator;
import org.everit.authentication.http.session.AuthenticationSessionAttributeNames;
import org.everit.authentication.http.session.SessionAuthentication;
import org.everit.authentication.http.session.ecm.SessionAuthenticationConstants;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM component for {@link Servlet}, {@link Filter} and {@link AuthenticationSessionAttributeNames}
 * interface based on {@link SessionAuthentication}.
 */
@Component(componentId = SessionAuthenticationConstants.SERVICE_FACTORYPID_SESSION_AUTHENTICATION,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    label = "Everit HTTP Session Authentication Component",
    description = "A component that implements HTTP session based authentication mechanism "
        + "as a Servlet Filter and supports logout.")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION,
        defaultValue = SessionAuthenticationConstants.DEFAULT_SERVICE_DESCRIPTION,
        priority = 1, label = "Service Description",
        description = "The description of this component configuration. It is used to easily "
            + "identify the service registered by this component.") })
public class SessionAuthenticationComponent {

  private AuthenticationPropagator authenticationPropagator;

  private String loggedOutUrl;

  private String reqParamNameLoggedOutUrl;

  private ServiceRegistration<?> serviceRegistration;

  private String sessionAttrNameAuthenticatedResourceId;

  /**
   * Activate method of component.
   */
  @Activate
  public void activate(final ComponentContext<SessionAuthenticationComponent> componentContext)
      throws Exception {
    SessionAuthentication sessionAuthentication =
        new SessionAuthentication(sessionAttrNameAuthenticatedResourceId, loggedOutUrl,
            reqParamNameLoggedOutUrl, authenticationPropagator);

    Dictionary<String, Object> serviceProperties =
        new Hashtable<>(componentContext.getProperties());
    serviceRegistration = componentContext
        .registerService(new String[] { Servlet.class.getName(), Filter.class.getName(),
            AuthenticationSessionAttributeNames.class.getName() },
            sessionAuthentication,
            serviceProperties);
  }

  /**
   * Component deactivate method.
   */
  @Deactivate
  public void deactivate() {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }
  }

  @ServiceRef(attributeId = SessionAuthenticationConstants.ATT_AUTHENTICATION_PROPAGATOR,
      defaultValue = "",
      attributePriority = SessionAuthenticationConstants.PRIORITY_05_AUTHENTICATION_PROPAGATOR,
      label = "Authentication Propagator OSGi filter",
      description = "OSGi Service filter expression for AuthenticationPropagator instance.")
  public void setAuthenticationPropagator(final AuthenticationPropagator authenticationPropagator) {
    this.authenticationPropagator = authenticationPropagator;
  }

  @StringAttribute(attributeId = SessionAuthenticationConstants.ATTR_LOGGED_OUT_URL,
      defaultValue = SessionAuthenticationConstants.DEFAULT_LOGGED_OUT_URL,
      priority = SessionAuthenticationConstants.PRIORITY_03_LOGGED_OUT_URL,
      label = "Logged out URL",
      description = "The URL where the browser will be redirected in case of logout.")
  public void setLoggedOutUrl(final String loggedOutUrl) {
    this.loggedOutUrl = loggedOutUrl;
  }

  @StringAttribute(attributeId = SessionAuthenticationConstants.ATTR_REQ_PARAM_NAME_LOGGED_OUT_URL,
      defaultValue = SessionAuthenticationConstants.DEFAULT_REQ_PARAM_NAME_LOGGED_OUT_URL,
      priority = SessionAuthenticationConstants.PRIORITY_04_REQ_PARAM_NAME_LOGGED_OUT_URL,
      label = "Logged out URL request parameter name",
      description = "The name of the request parameter that overrides the \"Logged out URL\" "
          + "configuration if present in the HTTP request.")
  public void setReqParamNameLoggedOutUrl(final String reqParamNameLoggedOutUrl) {
    this.reqParamNameLoggedOutUrl = reqParamNameLoggedOutUrl;
  }

  @StringAttribute(
      attributeId = SessionAuthenticationConstants.ATTR_SESSION_ATTR_NAME_AUTHENTICATED_RESOURCE_ID,
      defaultValue = SessionAuthenticationConstants.DEFAULT_SESSION_ATTR_NAME_AUTHENTICATED_RESOURCE_ID, // CS_DISABLE_LINE_LENGTH
      priority = SessionAuthenticationConstants.PRIORITY_02_SESSION_ATTR_NAME_AUTHENTICATED_RESOURCE_ID, // CS_DISABLE_LINE_LENGTH
      label = "Authenticated Resource ID session attribute name",
      description = "The name of the session attribute that stores the Resource ID of the "
          + "authenticated user.")
  public void setSessionAttrNameAuthenticatedResourceId(
      final String sessionAttrNameAuthenticatedResourceId) {
    this.sessionAttrNameAuthenticatedResourceId = sessionAttrNameAuthenticatedResourceId;
  }

}
