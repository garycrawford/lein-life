{{system-ns}}

{{system-comp-list}}

(defrecord Quotations-Web-System []
  component/Lifecycle
  (start [this]
    (component/start-system this components))
  (stop [this]
    (component/stop-system this components)))

{{{system-dep-graph}}}
