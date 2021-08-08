import com.google.gson.Gson
import com.tsukiseele.utils.OkHttpUtil
import com.tsukiseele.utils.getWindowsFileName
import okhttp3.Headers
import java.io.File

fun main(args: Array<String>) {
    val DOWNLOAD_DIR = File("./Download").absoluteFile
    val ALLOW_FILE = arrayOf(".mp3", ".jpg", ".png", ".txt")

    val gs = Gson()
    val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2FzbXIub25lIiwic3ViIjoidHN1a2lzZWVsZSIsImF1ZCI6Imh0dHBzOi8vYXNtci5vbmUvYXBpIiwibmFtZSI6InRzdWtpc2VlbGUiLCJncm91cCI6InVzZXIiLCJpYXQiOjE2MjcyMTgwMTUsImV4cCI6MTYyODQyNzYxNX0.4Ocg-vyWy3_7yL-O-BoIb4BSjkHQveJaf88c6_k2-7s"
    val token = authorization.replace("Bearer ", "")
    val headers = Headers.Builder()
        .add("authorization", authorization)
        .build()
    var page = 1
    while (true) {
        val url = "https://asmr.one/api/works?order=release&sort=desc&page=${page++}&seed=64&subtitle=0"
        try {
            OkHttpUtil.get(url, headers).body().also {
                val json = it!!.string()
                val data = gs.fromJson(json, MutableMap::class.java)
                val asmrList = data["works"] as MutableList<MutableMap<String, *>>
                asmrList.forEachIndexed { index, item ->
                    try {
                        val id = item["id"].toString().toFloat().toInt()
                        val name = item["title"].toString()
                        val trackUrl = "https://asmr.one/api/tracks/${id}"
                        val trackDir = DOWNLOAD_DIR.resolve("RJ${id}-${getWindowsFileName(name)}")
                        println("GET: ${trackUrl}")
                        OkHttpUtil[trackUrl, headers].body().also {
                            try {
                                val dirs = gs.fromJson(it!!.string(), MutableList::class.java)
// 目录解析开始
                                dirs.forEachIndexed continuing@  { index, item ->
                                    val dir = item as MutableMap<String, *>
                                    val title = item["title"].toString()
                                    val childDir = trackDir.resolve(getWindowsFileName(title))
                                    if (!dir.containsKey("children")) {
                                        return@continuing
                                    }
                                    val children = dir["children"] as MutableList<MutableMap<String, *>>
// 文件列表
                                    children.forEachIndexed next@ { index, item ->
                                        try {
                                            val fileName = item["title"].toString()
                                            val outFile = childDir.resolve(getWindowsFileName(fileName))
                                            if (outFile.exists()) {
                                                println("已存在，跳过：${outFile}")
                                                return@next
                                            }
//
                                            val hash = item["hash"].toString()
                                            val downloadApi = "https://asmr.one/api/media/download/${hash}?token=${token}"
                                            var isDownload = false
//
                                            run breaking@ {
                                                ALLOW_FILE.forEach {
                                                    if (fileName.endsWith(it)) {
                                                        isDownload = true
                                                        return@breaking
                                                    }
                                                }
                                            }
//
                                            if (isDownload) {
                                                OkHttpUtil[downloadApi, headers].body().also {
                                                    println("开始下载：${fileName}")
                                                    outFile.parentFile.mkdirs()
                                                    outFile.writeBytes(it!!.bytes())
                                                    println("下载完成：${outFile}")
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}