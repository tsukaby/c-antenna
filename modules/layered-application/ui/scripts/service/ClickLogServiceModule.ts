///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Service {
  "use strict";

  export class ClickLogService {

    constructor(public $http:ng.IHttpService) {
    }

    postClickLogArticle(article:Model.Article):void {
      var clickLog = new Model.ClickLog();
      clickLog.siteId = article.siteId;
      clickLog.articleId = article.id;

      this.postClickLog(clickLog);
    }

    postClickLogSite(site:Model.Site):void {
      var clickLog = new Model.ClickLog();
      clickLog.siteId = site.id;

      this.postClickLog(clickLog);
    }

    private postClickLog(clickLog:Model.ClickLog):void {
      this.$http.post("/api/click_log", clickLog).success((data:any) => {
        return true;
      });
    }
  }
}
