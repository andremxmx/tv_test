package com.example.livetvapp

data class Channel(
    val id: String,
    val name: String,
    val logo: String,
    val categorias: String,
    val key: String,
    val playbackUrl: String,
    val clearKeyJson: ClearKeyJson
)

data class ClearKeyJson(
    val keys: List<Key>,
    val type: String
)

data class Key(
    val kty: String,
    val kid: String,
    val k: String
)