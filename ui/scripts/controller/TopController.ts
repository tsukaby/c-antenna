///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module TopControllerModule {
  "use strict";

  export interface IScope extends ng.IScope {
    sites: Array<any>;

    totalItems: number;
    currentPage: number;

    maxSize:number;
    bigTotalItems:number;
    bigCurrentPage:number;

    // ページ変更時の処理 再検索
    pageChanged: Function;

    // 検索条件 ページング条件
    condition:Model.SimpleSearchCondition;

    // 検索処理
    loadData: () => void;

    postClickLogSite: Function;
    postClickLogArticle: Function;
  }

  export class TopController {

    constructor(public $scope:IScope, private $http:ng.IHttpService, $window:ng.IWindowService) {
      $scope.condition = new Model.SimpleSearchCondition();
      $scope.condition.page = 1;
      $scope.condition.count = 9;
      $scope.totalItems = 0;
      $scope.currentPage = 1;

      $scope.pageChanged = function () {
        $scope.condition.page = $scope.currentPage;
        this.loadData();
      };

      $scope.maxSize = 10;

      $scope.loadData = () => {
        this.$http.get("/api/sites?" + $.param(this.$scope.condition)).success((data:Model.Page<Model.Site>) => {
          this.$scope.sites = data.items;
          this.$scope.totalItems = data.total;
        });
      };

      // 初期データロード
      $scope.loadData();

      $scope.postClickLogSite = (site:Model.Site) => {
        var clickLog = new Model.ClickLog();
        clickLog.siteId = site.id;

        $http.post("/api/click_log", clickLog).success((data:any) => {
          return true;
        });

        $window.open(site.url, "_blank");

      };

      $scope.postClickLogArticle = (article:Model.Article) => {
        var clickLog = new Model.ClickLog();
        clickLog.siteId = article.siteId;
        clickLog.articleId = article.id;

        $http.post("/api/click_log", clickLog).success((data:any) => {
          return true;
        });

        $window.open(article.url, "_blank");

      };
    }


  }
}
