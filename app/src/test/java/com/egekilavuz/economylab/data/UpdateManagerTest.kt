package com.egekilavuz.economylab.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateManagerTest {

    @Test
    fun newerVersion_isDetected() {
        assertTrue(isNewerVersion("1.7.0", "1.6.2"))
    }

    @Test
    fun newerVersion_handlesVPrefix() {
        assertTrue(isNewerVersion("v1.7.0", "1.6.2"))
    }

    @Test
    fun sameVersion_isNotNewer() {
        assertFalse(isNewerVersion("1.6.2", "1.6.2"))
    }

    @Test
    fun olderVersion_isNotNewer() {
        assertFalse(isNewerVersion("1.5.9", "1.6.2"))
    }

    @Test
    fun multiDigitPartsAreComparedNumerically() {
        assertTrue(isNewerVersion("1.6.10", "1.6.9"))
        assertFalse(isNewerVersion("1.6.9", "1.6.10"))
    }

    @Test
    fun parseLatestRelease_extractsApk() {
        val json = """
            {
              "tag_name": "v1.7.0",
              "body": "Yenilikler",
              "assets": [
                {"name": "notes.txt", "browser_download_url": "https://example.com/notes.txt", "size": 10},
                {"name": "economy-lab-v1.7.0-debug.apk", "browser_download_url": "https://example.com/app.apk", "size": 12345}
              ]
            }
        """.trimIndent()

        val info = parseLatestRelease(json)
        assertNotNull(info)
        assertEquals("1.7.0", info!!.version)
        assertEquals("Yenilikler", info.notes)
        assertEquals("https://example.com/app.apk", info.apkUrl)
        assertEquals(12345L, info.apkSize)
    }

    @Test
    fun parseLatestRelease_withoutApkReturnsNull() {
        val json = """{"tag_name":"v1.7.0","assets":[{"name":"notes.txt","browser_download_url":"https://example.com/x"}]}"""
        assertNull(parseLatestRelease(json))
    }

    @Test
    fun parseLatestRelease_invalidJsonReturnsNull() {
        assertNull(parseLatestRelease("not-json"))
    }
}
