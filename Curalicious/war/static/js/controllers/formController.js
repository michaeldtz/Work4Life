/**
 * Created by michael on 19.10.14.
 */

angular.module('app.formController', ['ngAnimate', 'ngSanitize', 'textAngular'])

	.config(function(){
		
	})

    .run(function(){
    	
    })

    .controller('formController', function($scope, $http) {
    	
    	
    	$scope.sortnumUp = function(){
    		$scope.contentitem.sortorder += 10;
    	}
    	
    	$scope.sortnumDown = function(){
    		$scope.contentitem.sortorder -= 10;
    		if($scope.contentitem.sortorder < 10)
    			$scope.contentitem.sortorder = 10;
    	}
    	
    	$scope.deleteQuestion = function(question){
    		for (var i = 0; i < $scope.contentitem.quiz.questions.length; i++) {
    	        if ($scope.contentitem.quiz.questions[i] === question) {
    	        	$scope.contentitem.quiz.questions.splice(i, 1);
    	            return;
    	        }
    	    }
    	}
    	
    	
    	$scope.deleteAnswer = function(question, answer){
    		for (var i = 0; i < question.answers.length; i++) {
    	        if (question.answers[i] === answer) {
    	        	question.answers.splice(i, 1);
    	            return;
    	        }
    	    }
    	}
    	
    	$scope.deleteResult = function(result){
    		for (var i = 0; i < $scope.contentitem.quiz.results.length; i++) {
    	        if ($scope.contentitem.quiz.results[i] === result) {
    	        	$scope.contentitem.quiz.results.splice(i, 1);
    	            return;
    	        }
    	    }
    	}
    	
    	
    	
    	$scope.addQuestion = function(){
    		
    		$scope.contentitem.quiz.questions.push({
    			id: $scope.contentitem.quiz.questions.length + 1,
    			question : "",
    			weight : undefined,
    			answers : []		
    		});
    		
    		$scope.caluclateTotalPoints();
    	}
    	
    	$scope.addAnswer = function(question){
    	
    		question.answers.push({
    			id: question.answers.length + 1,
    			answer : "",
    			points : undefined,
    			
    		});
    		
    		$scope.caluclateTotalPoints();
    	}
    	
      	$scope.addResult = function(question){
        	
      		if($scope.contentitem.quiz.results == undefined){
      			$scope.contentitem.quiz.results = [];
      		}
      		
      		$scope.contentitem.quiz.results.push({
    			id: $scope.contentitem.quiz.results.length + 1,
    			from : 0,
    			to: 0,
    			title : "",
    			description : ""    			
    		});
    		
    		$scope.caluclateTotalPoints();
    	}
      	
      	
    	$scope.caluclateTotalPoints = function(){
    		var sum = 0;
    		for(var i in $scope.contentitem.quiz.questions){
    			var question = $scope.contentitem.quiz.questions[i];
    			
    			question.weight = parseInt(question.weight || 1);
    			
    			var qSum = 0;
    			for(var j in question.answers){
    				qSum += parseInt(question.answers[j].points || 0);
    			}
    			
    			sum += (qSum * question.weight);
    		} 
    		
    		$scope.totalpoints = sum;
    	}
    	
    });