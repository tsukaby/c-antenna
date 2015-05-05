var Model;
(function (Model) {
    "use strict";
    var Site = (function () {
        function Site() {
        }
        return Site;
    })();
    Model.Site = Site;
    var Article = (function () {
        function Article() {
        }
        return Article;
    })();
    Model.Article = Article;
    var SimpleSearchCondition = (function () {
        function SimpleSearchCondition() {
        }
        return SimpleSearchCondition;
    })();
    Model.SimpleSearchCondition = SimpleSearchCondition;
    var Sort = (function () {
        function Sort(key, order) {
            this.key = key;
            this.order = order;
        }
        return Sort;
    })();
    Model.Sort = Sort;
    (function (SortOrder) {
        SortOrder[SortOrder["Unknown"] = 0] = "Unknown";
        SortOrder[SortOrder["Asc"] = 1] = "Asc";
        SortOrder[SortOrder["Desc"] = 2] = "Desc";
    })(Model.SortOrder || (Model.SortOrder = {}));
    var SortOrder = Model.SortOrder;
    var Page = (function () {
        function Page() {
        }
        return Page;
    })();
    Model.Page = Page;
    var ClickLog = (function () {
        function ClickLog() {
        }
        return ClickLog;
    })();
    Model.ClickLog = ClickLog;
})(Model || (Model = {}));
var ArticleRankingControllerModule;
(function (ArticleRankingControllerModule) {
    "use strict";
    var ArticleRankingController = (function () {
        function ArticleRankingController($scope, $http, ngTableParams, $timeout, clickLogService) {
            this.$scope = $scope;
            this.$http = $http;
            this.ngTableParams = ngTableParams;
            this.$timeout = $timeout;
            this.clickLogService = clickLogService;
            $scope.clickLogService = clickLogService;
            $scope.data = new Model.Page();
            $scope.condition = new Model.SimpleSearchCondition();
            $scope.condition.page = 1;
            $scope.condition.count = 20;
            var d = new Date();
            d.setDate(d.getDate() - 1);
            $scope.condition.startDateTime = d.toISOString();
            $scope.condition.sort = new Model.Sort("CLICK_COUNT", 2 /* Desc */);
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 20
            }, {
                total: $scope.data.total,
                getData: function ($defer, params) {
                    $scope.condition.page = Number(params.url().page);
                    $scope.condition.count = Number(params.url().count);
                    $http.get("/api/articles?" + $.param($scope.condition)).success(function (data) {
                        $scope.data = data;
                        $timeout(function () {
                            params.total(data.total);
                            $defer.resolve(data.items);
                        }, 500);
                    });
                }
            });
        }
        return ArticleRankingController;
    })();
    ArticleRankingControllerModule.ArticleRankingController = ArticleRankingController;
})(ArticleRankingControllerModule || (ArticleRankingControllerModule = {}));
var TopControllerModule;
(function (TopControllerModule) {
    "use strict";
    var TopController = (function () {
        function TopController($scope, $http, clickLogService) {
            var _this = this;
            this.$scope = $scope;
            this.$http = $http;
            this.clickLogService = clickLogService;
            $scope.clickLogService = clickLogService;
            $scope.condition = new Model.SimpleSearchCondition();
            $scope.condition.page = 1;
            $scope.condition.count = 9;
            $scope.condition.sort = new Model.Sort("HATEBU_COUNT", 2 /* Desc */);
            $scope.totalItems = 0;
            $scope.currentPage = 1;
            $scope.pageChanged = function () {
                $scope.condition.page = $scope.currentPage;
                this.loadData();
            };
            $scope.maxSize = 10;
            $scope.loadData = function () {
                _this.$http.get("/api/sites?" + $.param(_this.$scope.condition)).success(function (data) {
                    _this.$scope.sites = data.items;
                    _this.$scope.totalItems = data.total;
                });
            };
            $scope.loadData();
        }
        return TopController;
    })();
    TopControllerModule.TopController = TopController;
})(TopControllerModule || (TopControllerModule = {}));
var LatestControllerModule;
(function (LatestControllerModule) {
    "use strict";
    var LatestController = (function () {
        function LatestController($scope, $http, ngTableParams, $timeout, clickLogService) {
            this.$scope = $scope;
            this.$http = $http;
            this.ngTableParams = ngTableParams;
            this.$timeout = $timeout;
            this.clickLogService = clickLogService;
            $scope.clickLogService = clickLogService;
            $scope.data = new Model.Page();
            $scope.condition = new Model.SimpleSearchCondition();
            $scope.condition.page = 1;
            $scope.condition.count = 20;
            $scope.tableParams = new ngTableParams({
                page: 1,
                count: 20
            }, {
                total: $scope.data.total,
                getData: function ($defer, params) {
                    $scope.condition.page = Number(params.url().page);
                    $scope.condition.count = Number(params.url().count);
                    $http.get("/api/articles?" + $.param($scope.condition)).success(function (data) {
                        $scope.data = data;
                        $timeout(function () {
                            params.total(data.total);
                            $defer.resolve(data.items);
                        }, 500);
                    });
                }
            });
        }
        return LatestController;
    })();
    LatestControllerModule.LatestController = LatestController;
})(LatestControllerModule || (LatestControllerModule = {}));
var Service;
(function (Service) {
    "use strict";
    var ClickLogService = (function () {
        function ClickLogService($http) {
            this.$http = $http;
        }
        ClickLogService.prototype.postClickLogArticle = function (article) {
            var clickLog = new Model.ClickLog();
            clickLog.siteId = article.siteId;
            clickLog.articleId = article.id;
            this.postClickLog(clickLog);
        };
        ClickLogService.prototype.postClickLogSite = function (site) {
            var clickLog = new Model.ClickLog();
            clickLog.siteId = site.id;
            this.postClickLog(clickLog);
        };
        ClickLogService.prototype.postClickLog = function (clickLog) {
            this.$http.post("/api/click_log", clickLog).success(function (data) {
                return true;
            });
        };
        return ClickLogService;
    })();
    Service.ClickLogService = ClickLogService;
})(Service || (Service = {}));
var Directive;
(function (Directive) {
    "use strict";
    var AdDirective = (function () {
        function AdDirective() {
            this.restrict = "A";
            this.templateUrl = "/partials/directive/ad.html";
        }
        return AdDirective;
    })();
    Directive.AdDirective = AdDirective;
})(Directive || (Directive = {}));
var Directive;
(function (Directive) {
    "use strict";
    var HeaderDirective = (function () {
        function HeaderDirective() {
            this.restrict = "A";
            this.templateUrl = "/partials/directive/header.html";
        }
        return HeaderDirective;
    })();
    Directive.HeaderDirective = HeaderDirective;
})(Directive || (Directive = {}));
var Directive;
(function (Directive) {
    "use strict";
    var SitePanelDirective = (function () {
        function SitePanelDirective() {
            this.restrict = "A";
            this.templateUrl = "/partials/directive/site_panel.html";
        }
        return SitePanelDirective;
    })();
    Directive.SitePanelDirective = SitePanelDirective;
})(Directive || (Directive = {}));
console.log("ignite!");
var App;
(function (App) {
    "use strict";
    App.appName = "cAntenna";
    angular.module(App.appName, ["ui.router", "ngTable", "ui.bootstrap", "angulartics", "angulartics.google.analytics", App.appName + ".controller", App.appName + ".service", App.appName + ".directive"], function ($stateProvider, $locationProvider) {
        $stateProvider.state('/', {
            url: "/",
            templateUrl: "partials/top.html",
            controller: "TopController"
        }).state('latest', {
            url: "/latest",
            templateUrl: "partials/latest.html",
            controller: "LatestController"
        }).state('article_ranking', {
            url: "/article_ranking",
            templateUrl: "partials/article_ranking.html",
            controller: "ArticleRankingController"
        });
        $locationProvider.html5Mode(true);
    }).run(function ($rootScope) {
        $rootScope.$on("$stateChangeSuccess", function () {
            $rootScope.$emit("$routeChangeSuccess");
        });
    });
    angular.module(App.appName + ".service", [], function () {
        false;
    }).factory("clickLogService", function ($http) {
        return new Service.ClickLogService($http);
    });
    angular.module(App.appName + ".controller", [App.appName + ".service"], function () {
        false;
    }).controller("ArticleRankingController", ArticleRankingControllerModule.ArticleRankingController).controller("TopController", TopControllerModule.TopController).controller("LatestController", LatestControllerModule.LatestController);
    angular.module(App.appName + ".directive", [], function () {
        false;
    }).directive("adDirective", function () { return new Directive.AdDirective(); }).directive("headerDirective", function () { return new Directive.HeaderDirective(); }).directive("sitePanelDirective", function () { return new Directive.SitePanelDirective(); });
})(App || (App = {}));
//# sourceMappingURL=Ignite.js.map