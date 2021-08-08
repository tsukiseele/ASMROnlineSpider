package com.tsukiseele.utils

import java.util.regex.Pattern
//\\/:*?"<>|
val WINDOWS_ILLEGAL_FILE = Pattern.compile("""[\\/:*?"<>|]""")// "[\\\\/:\\*\\?\\\"<>\\|]")
val WINDOWS_ILLEGAL_DIR = Pattern.compile("""[/:*?"<>|]""")// "[\\\\/:\\*\\?\\\"<>\\|]")

fun getWindowsFileName(text: String): String {
    // 将匹配到的非法字符以空替换
    return WINDOWS_ILLEGAL_FILE.matcher(text).replaceAll("_")
}
fun getWindowsDirName(text: String): String {
    // 将匹配到的非法字符以空替换
    return WINDOWS_ILLEGAL_DIR.matcher(text).replaceAll("_")
}