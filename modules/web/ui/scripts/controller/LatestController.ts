///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />
///<reference path='../Service.ts' />

module LatestControllerModule {
  "use strict";

  export interface IScope extends ng.IScope {
    // 検索結果
    articles:Array<Model.Article>;
    totalItems:number;

    currentPage: number;

    maxSize:number;

    // 検索条件 ページング条件
    condition:Model.SimpleSearchCondition;

    clickLogService:Service.ClickLogService;

    // ページ変更時の処理 再検索
    pageChanged: Function;

    // 検索処理
    loadData: () => void;
  }

  export class LatestController {

    constructor(public $scope:IScope,
                private $http:ng.IHttpService,
                private clickLogService:Service.ClickLogService) {

      $scope.clickLogService = clickLogService;

      $scope.articles = [];
      $scope.totalItems = 0;
      $scope.currentPage = 1;

      $scope.condition = new Model.SimpleSearchCondition();
      $scope.condition.page = 1;
      $scope.condition.count = 20;
      $scope.condition.hasEyeCatch = true;

      $scope.maxSize = 10;

      $scope.loadData = () => {
        $http.get("/api/articles?" + $.param($scope.condition)).success((data:Model.Page<Model.Article>) => {
          this.$scope.articles = data.items;
          this.$scope.totalItems = data.total;
        });
      };

      $scope.pageChanged = function () {
        $scope.condition.page = $scope.currentPage;
        this.loadData();
      };

      // 初期データロード
      $scope.loadData();

    }
  }
}
