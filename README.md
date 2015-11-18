authentication-http-session-ecm
===============================

ECM based components for [authentication-http-session][0].

#Component
The module contains one ECM based component. The component can be 
instantiated multiple times via Configuration Admin. The component registers 
three OSGi services:
 - **AuthenticationSessionAttributeNames**: Provides the HTTP Session 
 attribute names used by the authentication components. This interface is 
 used by other components if they need to store authentication related 
 attributes in the session, for e.g. the Resource ID of an authenticated user.
 - **javax.servlet.Filter**: Checks if the HTTP Session contains an 
 Authenticated Resource ID. The attribute name for the Authenticated Resource 
 ID is provided by the 
 *AuthenticationSessionAttributeNames.authenticatedResourceId()* method. If 
 there is a *non-null* value assigned to this attribute name, it executes 
 the authenticated process in the name of the Authenticated Resource. This 
 means it invokes further the filter chain via an *AuthenticationPropagator* 
 provided by the [authentication-context-api][1]. If there is no Authenticated 
 Resource ID available in the session, the filter chain will be processed 
 further without any special extension.
 - **javax.servlet.Servlet**: Invalidates the HTTP Session of the request and 
 redirects to a specified location. This ends the session of the user so 
 he/she is logged out from the system.

#Configuration
 - **Authenticated Resource ID session attribute name**: The name of the 
 session attribute that stores the Resource ID of the authenticated user. 
 This value will be provided by the 
 *AuthenticationSessionAttributeNames.authenticatedResourceId()* method.
 - **Logged out URL**: The URL where the browser will be redirected by default 
 in case of invoking the logout *Servlet*.
 - **Logged out URL request parameter name**: The name of the request 
 parameter that overrides the *Logged out URL* configuration if present in the 
 HTTP request.

[0]: https://github.com/everit-org/authentication-http-session
[1]: https://github.com/everit-org/authentication-context-api
