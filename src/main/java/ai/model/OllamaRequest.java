package ai.model;

public class OllamaRequest {

    private String model;

    private boolean think;

    private String prompt;

    private boolean stream;

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
