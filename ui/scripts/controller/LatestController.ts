///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Sample {
  "use strict";

  export interface IScope extends ng.IScope {
    tableParams: any;

    // 検索条件 ページング条件
    condition:Model.SimpleSearchCondition;
  }

  export class LatestController {

    constructor(public $scope:IScope, private $http:ng.IHttpService, private ngTableParams:any) {

      $scope.condition = new Model.SimpleSearchCondition();
      $scope.condition.page = 1;
      $scope.condition.count = 20;

      $http.get("/api/lately_articles?" + $.param($scope.condition)).success((data:Model.Page<Model.Article>) => {
        $scope.tableParams = new ngTableParams({
          page: 1, //初期ページ
          count: 20 //初期件数
        }, {
          total: data.total, // length of data
          getData: function ($defer:any, params:any) {
            $defer.resolve(data.items.slice((params.page() - 1) * params.count(), params.page() * params.count()));
          }
        });
      });
    }

  }
}
