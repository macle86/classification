package io.runon.classification.evaluation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 훈련과 테스트 평가정보
 * 최상단에서 하위정보를 포함하고 있는경우에 사용
 * @author macle
 */
public class TradingTestEvaluations extends TradingTestEvaluation{

    private final TradingTestEvaluation [] tradingTestEvaluations;

    public TradingTestEvaluations(TradingTestEvaluation [] tradingTestEvaluations){
        this.tradingTestEvaluations = tradingTestEvaluations;

        ClassificationEvaluation[] evaluations = new ClassificationEvaluation[2];
        evaluations[0] = new ClassificationEvaluation();
        evaluations[0].setName("trading");
        evaluations[1] = new ClassificationEvaluation();
        evaluations[1].setName("test");
        this.evaluations = evaluations;

        sum();
    }

    public void sum(){
        for (int i = 0; i <evaluations.length ; i++) {
            ClassificationEvaluation evaluation = evaluations[i];
            evaluation.init();
            for(TradingTestEvaluation tradingTestEvaluation : tradingTestEvaluations){
                evaluation.add(tradingTestEvaluation.evaluations[i]);
            }
        }

    }

    public TradingTestEvaluation[] getTradingTestEvaluations() {
        return tradingTestEvaluations;
    }

    @Override
    public JsonObject toJsonObject(){
        JsonObject jsonObject = super.toJsonObject();
        JsonArray child = new JsonArray();
        for(TradingTestEvaluation evaluation :tradingTestEvaluations){
            child.add(evaluation.toJsonObject());
        }

        jsonObject.add("child_evaluations",child);


        return jsonObject;
    }

    private Map<String, TradingTestEvaluation> map = null;

    public void makeMap(){
        map = new HashMap<>();
        for(TradingTestEvaluation tradingTestEvaluation : tradingTestEvaluations){
            map.put(tradingTestEvaluation.id, tradingTestEvaluation);
        }
    }

    public TradingTestEvaluation getEvaluation(String id){
        if(map == null){
            makeMap();
        }
        return map.get(id);
    }

}
