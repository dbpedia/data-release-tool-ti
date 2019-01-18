/*-
 * #%L
 * databus-maven-plugin
 * %%
 * Copyright (C) 2018 Sebastian Hellmann (on behalf of the DBpedia Association)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.dbpedia.databus


import org.dbpedia.databus.params.{BaseEntity => ScalaBaseEntity}

import scala.collection.JavaConverters._
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.TemporalAccessor
import java.time._


trait Parameters {
  this: Properties =>

  lazy val params = new Parameters(this)

  val ISO_INSTANT_NO_NANO = new DateTimeFormatterBuilder().parseCaseInsensitive().appendInstant(0).toFormatter

  class Parameters(props: Properties) {


    val invocationTime: ZonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())


    lazy val issuedDate: ZonedDateTime =
      try {
        if (props.tryVersionAsIssuedDate) {
          val attempt = props.version.replace(".", "-") + "T00:00:00"
          val zone = ZonedDateTime.ofInstant(LocalDateTime.parse(attempt).toInstant(ZoneOffset.UTC), ZoneId.systemDefault())
          zone
        } else {
          ZonedDateTime.ofInstant(LocalDateTime.parse(props.issuedDate).toInstant(ZoneOffset.UTC), ZoneId.systemDefault())
        }
      }catch {case e:Throwable => {invocationTime}}


    lazy val modifiedDate: ZonedDateTime = try {
      ZonedDateTime.ofInstant(LocalDateTime.parse(props.modifiedDate).toInstant(ZoneOffset.UTC), ZoneId.systemDefault())
    } catch {
      case e: Throwable => invocationTime
    }

    lazy val wasDerivedFrom = props.wasDerivedFrom.asScala.map(ScalaBaseEntity.fromJava).toSet

    lazy val versionToInsert = if (insertVersion) Some(version) else None
  }

}


