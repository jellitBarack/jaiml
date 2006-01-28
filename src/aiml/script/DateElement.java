package aiml.script;

import aiml.classifier.MatchState;

public class DateElement extends EmptyElement {

  public String evaluate(MatchState m) {
    return "date()";
  }

  public String execute(MatchState m) {
    return "print(date())";
  }

  public String toString() {
    return "date()";
  }

}