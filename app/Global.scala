import com.tsukaby.c_antenna.batch.CrawlActor
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    super.onStart(app)

    CrawlActor.runCrawler

  }
}
