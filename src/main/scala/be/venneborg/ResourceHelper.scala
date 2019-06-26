package be.venneborg

import java.io.{ByteArrayInputStream, InputStream}

import com.typesafe.sbt.web.incremental
import com.typesafe.sbt.web.incremental.{OpResult, OpSuccess}
import sbt._

import scala.io.Source

object ResourceHelper {

  def copyResourcesTo(to: File, urls: Seq[URL], cacheDir: File): File = {
    copyResourcesWith(to, urls, cacheDir)
  }

  private def copyResourcesWith(to: File, ops: Seq[URL], cacheDir: File): File = {
    incremental.syncIncremental(cacheDir, ops) {
      ops =>

        var lastFile: File = null
        (ops.map { url =>
            val fromFile =  if (url.getProtocol == "file") {
              new File(url.toURI)
            } else if (url.getProtocol == "jar") {
              new File(url.getFile.split('!')(1))
            } else {
              throw new RuntimeException(s"Unknown protocol: $url")
            }

            val toFile = to / fromFile.getName
            lastFile = toFile
//            println(s"copy $fromFile -> $toFile")

            val is = url.openStream()
            try {
              toFile.getParentFile.mkdirs()
              IO.transfer(is, toFile)
              url -> OpSuccess(Set(fromFile), Set(toFile))
            } finally is.close()
        }.toMap, lastFile)
    }._2
  }

  private def toInputStream(text: String): InputStream = {
    new ByteArrayInputStream(text.getBytes("UTF-8"))
  }

  private def removeExecutableLines(input: InputStream): InputStream = {
    val encoding = "UTF-8"
    val lines = Source.fromInputStream(input, encoding).getLines().toStream
    val reverted = lines.reverse
    val lastLine #:: other = reverted
    val filtered = other.dropWhile(!_.trim.startsWith("TypeScript"))
    val resultText = filtered.reverse.mkString("\n") + lastLine
    new ByteArrayInputStream(resultText.getBytes(encoding))
  }

}
