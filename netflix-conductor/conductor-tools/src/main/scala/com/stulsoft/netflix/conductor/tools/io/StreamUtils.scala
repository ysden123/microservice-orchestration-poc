/*
 * Copyright (c) 2019. Yuriy Stul
 */

package com.stulsoft.netflix.conductor.tools.io

import java.io.{File, FileFilter, FileInputStream, InputStream}

import com.typesafe.scalalogging.LazyLogging

/**
 * @author Yuriy Stul
 */
object StreamUtils extends LazyLogging {
  /**
   * Returns a list of InputStream from files in a folder according to file names matching to filename name suffix
   *
   * @param fileNameSuffix the file name suffix; empty - all files
   * @param folder         specifies a folder; default value is "."
   * @return the list of InputStream from files in a folder according to file names matching to filename name suffix
   */
  def inputStreams(fileNameSuffix: String, folder: String = "."): Seq[InputStream] = {
    new File(folder)
      .listFiles(new FileFilter {
        override def accept(pathname: File): Boolean = pathname.isFile && pathname.getName.endsWith(fileNameSuffix)
      })
      .map(file => new FileInputStream(file.getName))
  }
}
