package org.shadowrunrussia2020.android.common.models

class LoginRequest(var email: String, var password: String, var firebase_token: String)

class LoginResponse(var id: Int, var api_key: String)

class SaveTokenRequest(var id: Int, var token: String)
