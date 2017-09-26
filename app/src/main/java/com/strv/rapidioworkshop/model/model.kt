package com.strv.rapidioworkshop.model


data class User(
        val displayName: String
)

data class Channel(
        val lastMessageId: String
)

data class Message(
        val text: String,
        val channelId: String,
        val userDisplayName: String,
        val timestamp: Long
)