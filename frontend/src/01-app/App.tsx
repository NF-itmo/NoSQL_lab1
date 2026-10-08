import { HomePage } from "@/02-pages/home";
import { ErrorNotifier } from "@/06-shared/lib/errorNotifier";
import "./App.css";
import "./settings.css";

const App = () => {
  return (
    <ErrorNotifier>
      <HomePage/>
    </ErrorNotifier>
  );
}

export default App;
