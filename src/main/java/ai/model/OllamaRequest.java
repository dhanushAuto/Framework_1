package ai.model;

public class OllamaRequest {

    private String model;

    private boolean think;

    private String prompt;

    private boolean stream;
    private Options options;

    public static class Options {
        private int num_predict;

        public int getNum_predict() {
            return num_predict;
        }

        public void setNum_predict(int num_predict) {
            this.num_predict = num_predict;
        }
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isStream() {
        return stream;
    }

    public boolean isThink() {
        return think;
    }

    public void setThink(boolean think) {
        this.think = think;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }
}
