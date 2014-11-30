import akka.actor.Props
import com.tsukaby.c_antenna.batch._
import com.tsukaby.c_antenna.cache.VolatilityCache
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings}
import us.theatr.akka.quartz.{AddCronSchedule, QuartzActor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    super.onStart(app)

    // 各サイトをクロールしてRSSを最新に保つバッチ実行登録
    Akka.system.scheduler.schedule(10.seconds, 10.minutes, Akka.system.actorOf(Props[RssCrawlActor]), "")

    // クリックのランキングを保存するバッチ実行登録
    Akka.system.scheduler.schedule(5.minutes, 5.minutes, Akka.system.actorOf(Props[RankingActor]), "")

    val quartzActor = Akka.system.actorOf(Props[QuartzActor])

    // サイト名を最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(Akka.system.actorOf(Props[SiteNameActor]), "0 0 3 * * ?", "Refresh site name")

    // サイト名を最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(Akka.system.actorOf(Props[SiteThumbnailActor]), "0 0 4 * * ?", "Refresh site thumbnail")

    // サイトランキングを最新に保つバッチ実行登録
    quartzActor ! AddCronSchedule(Akka.system.actorOf(Props[HatebuActor]), "0 0 4 * * ?", "Refresh site name")

    // キャッシュ削除
    VolatilityCache.flushDB()

  }
}
