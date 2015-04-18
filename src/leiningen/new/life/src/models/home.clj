(ns {{ns-name}}.models.home)

(def ^:private about-model-store (atom nil))

(defn about-model
  []
  @about-model-store)

(defn about-model!
  [model]
  (swap! about-model-store (fn [_] model)))
