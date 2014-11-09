///<reference path='../typings/angularjs/angular.d.ts' />
///<reference path='../typings/angular-ui/angular-ui-router.d.ts' />

///<reference path='Model.ts' />
///<reference path='Service.ts' />
///<reference path='Controller.ts' />

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
    ["ui.router", "ngTable", "ui.bootstrap", "angulartics", "angulartics.google.analytics", appName + ".controller", appName + ".service", appName + ".filter", appName + ".directive"],
    ($stateProvider:ng.ui.IStateProvider, $locationProvider:ng.ILocationProvider)=> {
      $stateProvider
        .state('/', {
          url: "/",
          templateUrl: "partials/top.html",
          controller: "TopController"
        })
        .state('latest', {
          url: "/latest",
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
    .factory("sampleService", ($http:ng.IHttpService):Service.SampleService=> {
      return new Service.SampleService($http);
    })
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
  angular.module(
    appName + ".directive",
    [],
    ()=> {
      false;
    }
  )
    .directive("tgFileBind", ()=> {
      return (scope:any, elm:any, attrs:any) => {
        elm.bind("change", (evt:any) => {
          scope.$apply((scope:any)=> {
            scope[attrs.name] = evt.target.files;
          });
        });
      };
    })
    .directive("tgContenteditable", ($parse:ng.IParseService)=> {
      return {
        require: "ngModel",
        link: (scope:any, elm:any, attrs:any, ctrl:ng.INgModelController) => {
          var value = $parse(attrs.ngModel)(scope);

          elm.attr("contenteditable", "");
          // view -> model
          var viewToModel = () => {
            scope.$apply(()=> {
              ctrl.$setViewValue(elm.html());
            });
          };
          elm.bind("blur", viewToModel);
          elm.bind("keyup", viewToModel);
          elm.bind("keydown", viewToModel);

          // model -> view
          ctrl.$render = () => {
            elm.html(ctrl.$viewValue);
          };

          // load init value from DOM
          if (value) {
            ctrl.$setViewValue(value);
            ctrl.$render();
          } else {
            ctrl.$setViewValue(elm.html());
          }
        }
      };
    })
  ;

  // モジュールの定義。filterに関するモジュール。
  angular.module(
    appName + ".filter",
    [],
    ()=> {
      false;
    }
  )
  /**
   * 指定した要素を {@type Array} 内から除外するフィルタ。
   * @function
   * @param {Array|Object} options
   * @param {Array} [options.exclude] 除外する対象
   * @param {function} [options.compare]
   */
    .filter("rmDuplicated", ()=> {
      return (input:any[], options:any)=> {
        if (angular.isUndefined(input)) {
          return input;
        } else if (!angular.isArray(input)) {
          console.error("input is not array.", input);
          return input;
        }
        var excludeList:any;
        if (angular.isUndefined(options)) {
          console.error("options is required.");
          return input;
        } else if (angular.isArray(options)) {
          excludeList = options;
        } else if (angular.isArray(options.exclude)) {
          excludeList = options.exclude;
        }
        var compareFn = (a:any, b:any) => {
          return a.$key.keystr === b.$key.keystr;
        };
        if (angular.isUndefined(options)) {
          false;
        } else if (angular.isFunction(options.compare)) {
          compareFn = options.compare;
        }

        var result:any[] = [];
        input.forEach((data)=> {
          if (!excludeList.some((exclude:any) => compareFn(data, exclude))) {
            result.push(data);
          }
        });

        return result;
      };
    }
  )
  ;
}
