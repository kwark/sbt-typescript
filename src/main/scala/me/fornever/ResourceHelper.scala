package me.fornever

import com.typesafe.sbt.web.incremental
import com.typesafe.sbt.web.incremental.{OpResult, OpSuccess}
import sbt._

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
          val is = url.openStream()
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

}
