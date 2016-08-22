package org.paxdb.api.war

import java.net.{HttpURLConnection, URL}

import org.junit.Assert._
import org.junit._

import scala.collection.JavaConversions._
import scala.util.parsing.json._
import scala.xml.XML

class SearchResourceTest {

    val searchUrl = new URL("http://localhost:9095/api/search?q=cdc");
    var connection: HttpURLConnection = _
    val LATEST = "3"

    @Before
    def before() {
        connection = searchUrl.openConnection().asInstanceOf[HttpURLConnection];
        connection.setRequestMethod("GET");
    }

    @After
    def after() = connection.disconnect


    @Test
    def testJson() = {
        connection.setRequestProperty("Accept", "application/vnd.paxdb.search+json;version=" + LATEST);

        val jsonResponse: String = Util.instance.readFile(connection.getInputStream()).mkString(" ");
        val Some(M(res)) = JSON.parseFull(jsonResponse)
        val M(sr) = res("searchResponse")
        val M(hits) = sr("hits")
        assertTrue(hits.keys.containsAll(List("@pageSize", "@totalHits", "@currentPage", "protein", "@query")))
        assertEquals("cdc", hits("@query"))
        assertTrue(hits("@totalHits").asInstanceOf[String].toInt > 0)
        val L(proteins) = hits("protein")
        assertTrue(proteins.size > 0)
        val M(p) = proteins.head
        assertTrue(p.keys.toString(), p.keys.containsAll(List("highlighted", "speciesId", "@name", "annotation", "@id")))

        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
    }

    @Test
    def testXml() = {

        connection.setRequestProperty("Accept", "application/vnd.paxdb.search+xml;version=" + LATEST);
        val res = XML.load(connection.getInputStream())

        assertNotNull(res.attribute("version").get)
        res match {
            case <searchResponse><hits>{proteins @ _* }</hits></searchResponse> =>
                for (p @ <protein>{_*}</protein> <-proteins ) {
                  assertTrue("missing id/name attributes: " + p.attributes,p.attributes.asAttrMap.keys.containsAll(List("id", "name")));
                  assertNotNull("id attr shouldn't be null",p.attribute("id"))
                  assertNotNull("name attr shouldn't be null",p.attribute("name"))
                };
            case _ => throw new Exception("bad xml" + res)
        }
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
    }
}

//helper extractors
class CC[T] {
    def unapply(a: Any): Option[T] = Some(a.asInstanceOf[T])
}

object M extends CC[Map[String, Any]]

object L extends CC[List[Any]]

object S extends CC[String]


