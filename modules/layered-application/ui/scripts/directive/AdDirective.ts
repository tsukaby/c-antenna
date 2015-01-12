///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Directive {
  "use strict";

  export class AdDirective implements ng.IDirective {
    restrict:string = "A";
    templateUrl: string = "/partials/directive/ad.html";
  }
}
