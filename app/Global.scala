import com.tsukaby.c_antenna.batch.CrawlActor
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    super.onStart(app)

    // 各サイトをクロールしてRSSを最新に保つバッチ実行
    CrawlActor.runRssCrawler
    
    // サイト名を最新に保つバッチ実行
    CrawlActor.runSiteNameMaintainer

  }
}
