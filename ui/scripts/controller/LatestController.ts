///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module LatestControllerModule {
  "use strict";

  export interface IScope extends ng.IScope {
    tableParams: any;

    // 検索結果
    data:Model.Page<Model.Article>;

    // 検索条件 ページング条件
    condition:Model.SimpleSearchCondition;
  }

  export class LatestController {

    constructor(public $scope:IScope, private $http:ng.IHttpService, private ngTableParams:any, private $timeout:ng.ITimeoutService) {
      $scope.data = new Model.Page<Model.Article>();

      $scope.condition = new Model.SimpleSearchCondition();
      $scope.condition.page = 1;
      $scope.condition.count = 20;

      $scope.tableParams = new ngTableParams({
        page: 1, //初期ページ
        count: 20 //初期件数
      }, {
        total: $scope.data.total, // length of data
        getData: function ($defer:any, params:any) {

          // 検索条件が変更された場合
          $scope.condition.page = Number(params.url().page);
          $scope.condition.count = Number(params.url().count);

          $http.get("/api/lately_articles?" + $.param($scope.condition)).success((data:Model.Page<Model.Article>) => {
            $scope.data = data;

            $timeout(() => {
              // 最大件数更新
              params.total(data.total);
              // データ更新
              $defer.resolve(data.items);
            }, 500);
          });
        }
      });
    }

  }
}
