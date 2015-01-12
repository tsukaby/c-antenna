///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Directive {
  "use strict";

  export class HeaderDirective implements ng.IDirective {
    restrict:string = "A";
    templateUrl: string = "/partials/directive/header.html";
  }
}
