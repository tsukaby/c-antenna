///<reference path='../typings/angularjs/angular.d.ts' />
///<reference path='../typings/angular-ui/angular-ui-router.d.ts' />

///<reference path='Model.ts' />
///<reference path='Service.ts' />
///<reference path='Controller.ts' />
///<reference path='Directive.ts' />

console.log("ignite!");

/**
 * モジュールの作成や動作の定義。
 */
module App {
  "use strict";

  export var appName = "cAntenna";

  // モジュールの定義
  angular.module(
    appName,
    [
      "ui.router",
      "ngTable",
      "ui.bootstrap",
      "angulartics",
      "angulartics.google.analytics",
      "masonry",
      appName + ".controller",
      appName + ".service",
      appName + ".directive"
    ],
    ($stateProvider:ng.ui.IStateProvider, $locationProvider:ng.ILocationProvider)=> {
      $stateProvider
        .state('/', {
          url: "/?page",
          templateUrl: "partials/top.html",
          controller: "TopController"
        })
        .state('latest', {
          url: "/latest?page",
          templateUrl: "partials/latest.html",
          controller: "LatestController"
        })
        .state('article_ranking', {
          url: "/article_ranking",
          templateUrl: "partials/article_ranking.html",
          controller: "ArticleRankingController"
        });
      // hashの書き換えの代わりにHTML5のHistory API関係を使うモードを設定する。
      $locationProvider.html5Mode(true);
    }
  )
    // モジュールとして登録する。angular.module() -> .config() -> .run() で1セット。
    .run(($rootScope:ng.IRootScopeService)=> {
      $rootScope.$on("$stateChangeSuccess", () => {
        $rootScope.$emit("$routeChangeSuccess");
      });
    })
  ;

  // モジュールの定義。
  angular.module(
    // モジュール名
    appName + ".service",
    // 依存モジュールはなし
    [],
    // .configで設定する項目はなし
    ()=> {
      false;
    }
  )
    .factory("clickLogService", ($http:ng.IHttpService):Service.ClickLogService => {
      return new Service.ClickLogService($http);
    })
  ;

  angular.module(
    appName + ".controller",
    [appName + ".service"],
    ()=> {
      false;
    }
  ).controller("ArticleRankingController", ArticleRankingControllerModule.ArticleRankingController)
    .controller("TopController", TopControllerModule.TopController)
    .controller("LatestController", LatestControllerModule.LatestController)
  ;

  // モジュールの定義。directiveに関するモジュール。
  angular.module(appName + ".directive", [], () => {
    false;
  })
    .directive("adDirective", () => new Directive.AdDirective())
    .directive("headerDirective", () => new Directive.HeaderDirective())
    .directive("sitePanelDirective", () => new Directive.SitePanelDirective())
  ;

}
