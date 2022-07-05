package io.runon.classification.evaluation;

/**
 * 훈련과 테스트 평가정보
 * @author macle
 */
public class TradingTestEvaluation extends MultinomialEvaluation{

    public TradingTestEvaluation() {
        ClassificationEvaluation[] evaluations = new ClassificationEvaluation[2];
        evaluations[0] = new ClassificationEvaluation();
        evaluations[0].setName("trading");
        evaluations[1] = new ClassificationEvaluation();
        evaluations[1].setName("test");

        this.evaluations = evaluations;
    }

    public ClassificationEvaluation getTradingEvaluation(){
        return evaluations[0];
    }

    public ClassificationEvaluation getTestEvaluation(){
        return evaluations[1];
    }


}
