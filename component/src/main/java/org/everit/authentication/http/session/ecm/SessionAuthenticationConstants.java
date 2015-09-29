/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.biz)
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
package org.everit.authentication.http.session.ecm;

/**
 * Constants of the Session Authentication component.
 */
public final class SessionAuthenticationConstants {

  public static final String ATT_AUTHENTICATION_PROPAGATOR = "authenticationPropagator.target";

  public static final String ATTR_LOGGED_OUT_URL = "logged.out.url";

  public static final String ATTR_REQ_PARAM_NAME_LOGGED_OUT_URL = "req.param.name.logged.out.url";

  public static final String ATTR_SESSION_ATTR_NAME_AUTHENTICATED_RESOURCE_ID =
      "session.attr.name.authenticated.resource.id";

  public static final String DEFAULT_LOGGED_OUT_URL = "/logged-out.html";

  public static final String DEFAULT_REQ_PARAM_NAME_LOGGED_OUT_URL = "loggedOutUrl";

  public static final String DEFAULT_SERVICE_DESCRIPTION =
      "Default Session Authentication Component";

  public static final String DEFAULT_SESSION_ATTR_NAME_AUTHENTICATED_RESOURCE_ID =
      "authenticated.resource.id";

  public static final int PRIORITY_01_SERVICE_DESCRITION = 1;

  public static final int PRIORITY_02_SESSION_ATTR_NAME_AUTHENTICATED_RESOURCE_ID = 2;

  public static final int PRIORITY_03_LOGGED_OUT_URL = 3;

  public static final int PRIORITY_04_REQ_PARAM_NAME_LOGGED_OUT_URL = 4;

  public static final int PRIORITY_05_AUTHENTICATION_PROPAGATOR = 5;

  /**
   * The service factory PID of the Session Authentication component.
   */
  public static final String SERVICE_FACTORYPID_SESSION_AUTHENTICATION =
      "org.everit.authentication.http.session.ecm.SessionAuthentication";

  private SessionAuthenticationConstants() {
  }

}
