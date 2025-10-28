package com.example.notey

import java.util.concurrent.TimeUnit
import java.time.Instant // Requires minimum API 26 (use Calendar if supporting older APIs)

// Utility function to convert Long timestamp to a human-readable relative time string
fun formatRelativeTime(timestamp: Long): String {
    // Current time in milliseconds
    val now = System.currentTimeMillis()
    // The difference in time
    val diff = now - timestamp

    return when {
        // Less than 1 minute ago
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"

        // Less than 1 hour ago
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            "$minutes min ago"
        }

        // Less than 24 hours ago
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            "$hours hour${if (hours > 1) "s" else ""} ago"
        }

        // Yesterday
        diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"

        // Less than 7 days ago
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            "$days day${if (days > 1) "s" else ""} ago"
        }

        // More than a week ago, show the full date (e.g., Oct 20)
        else -> {
            // Using Instant.ofEpochMilli(timestamp) for modern date formatting
            // Note: For simplicity, we'll use a basic date format here.
            java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
        }
    }
}