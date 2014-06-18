package me.fornever

import java.io.{ByteArrayInputStream, InputStream, SequenceInputStream}

import com.typesafe.sbt.web.incremental
import com.typesafe.sbt.web.incremental.{OpResult, OpSuccess}
import sbt._

import scala.collection.JavaConversions.asJavaEnumeration
import scala.io.Source

object ResourceHelper {

  /**
   * Copy a resource to a target folder. It is a fixed version of method SbtWeb.copyResourceTo which saves the original
   * file name and extension.
   *
   * The resource won't be copied if the new file is older.
   *
   * @param to the target folder.
   * @param url the url of the resource.
   * @param cacheDir the dir to cache whether the file was read or not.
   * @return the copied file.
   */
  def copyResourceTo(to: File, url: URL, cacheDir: File): File = {
    copyResourceWith(to, url, cacheDir, url.openStream())
  }

  /**
   * Wraps TypeScript BatchCompiler and another modules and copies them to output file. Inspired by
   * https://www.npmjs.org/package/typescript-compiler-exposed
   * @param to the target folder.
   * @param url the URL of the resource.
   * @param cacheDir the dir to cache whether the file was read or not.
   * @return the created module file.
   */
  def wrapTypescriptModuleAndCopyTo(to: File, url: URL, cacheDir: File): File = {
    copyResourceWith(to, url, cacheDir,
      new SequenceInputStream(
        Seq(
          toInputStream("(function() {\n"),
          removeExecutableLines(url.openStream()),
          toInputStream("module.exports = TypeScript;\n})();")
        ).iterator
      )
    )
  }

  private def copyResourceWith(to: File, url: URL, cacheDir: File, streamGetter: => InputStream): File = {
    incremental.runIncremental(cacheDir, Seq(url)) {
      ops =>
        val fromFile = if (url.getProtocol == "file") {
          new File(url.toURI)
        } else if (url.getProtocol == "jar") {
          new File(url.getFile.split('!')(1))
        } else {
          throw new RuntimeException(s"Unknown protocol: $url")
        }

        val toFile = to / fromFile.getName

        if (ops.nonEmpty) {
          val is = streamGetter
          try {
            toFile.getParentFile.mkdirs()
            IO.transfer(is, toFile)
            (Map(url -> OpSuccess(Set(fromFile), Set(toFile))), toFile)
          } finally is.close()
        } else {
          (Map.empty[URL, OpResult], toFile)
        }
    }
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
    val result = filtered.reverse.append(lastLine)
    val resultText = result.mkString("\n")
    new ByteArrayInputStream(resultText.getBytes(encoding))
  }

}
