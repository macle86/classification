package io.runon.classification.evaluation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 훈련과 테스트 평가정보
 * 다항분류 기준일때
 * @author macle
 */
public class TradingTestEvaluation extends MultinomialEvaluation{

    private final MultinomialEvaluation tradingEvaluation, testEvaluation;

    public TradingTestEvaluation(ClassificationEvaluation [] evaluations) {
        super(evaluations);

        tradingEvaluation = new MultinomialEvaluation(copy(evaluations));
        testEvaluation = new MultinomialEvaluation(copy(evaluations));
    }

    private ClassificationEvaluation [] copy(ClassificationEvaluation [] evaluations){

        return evaluations;
    }

    public void add(boolean isTrading, String classificationId, String trueId ){
        this.add(classificationId, trueId);
        if(isTrading){
            tradingEvaluation.add(classificationId, trueId);
        }else{
            testEvaluation.add(classificationId, trueId);
        }
    }

    public MultinomialEvaluation getTradingEvaluation() {
        return tradingEvaluation;
    }

    public MultinomialEvaluation getTestEvaluation() {
        return testEvaluation;
    }

    @Override
    public String toString(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(toJsonObject());
    }


    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();
        if(id != null){
            jsonObject.addProperty("id",id);
        }
        if(name != null){
            jsonObject.addProperty("name", name);
        }

        if(tradingEvaluation.length() == 0 && testEvaluation.length() == 0){
            return jsonObject;
        }

        if(testEvaluation.length() == 0){

        }



        JsonObject trading = new JsonObject();
        trading.addProperty("accuracy", tradingEvaluation.accuracy());
        trading.addProperty("f1_score", tradingEvaluation.f1Score());
        trading.addProperty("geometric_mean", tradingEvaluation.geometricMean());
        trading.addProperty("p", tradingEvaluation.getPositive());
        trading.addProperty("n", tradingEvaluation.getNegative());

        JsonArray array = new JsonArray();
        for(ClassificationEvaluation evaluation :evaluations){
            array.add(evaluation.toJsonObject());
        }

        jsonObject.add("evaluations", array);

        return jsonObject;
    }

}
