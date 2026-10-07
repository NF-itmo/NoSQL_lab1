import { ErrorNotifier } from "@/06-shared/lib/errorNotifier";
import styles from "./index.module.css"
import { Topbar } from "@/03-widgets/topbar";

type Props = {};

export const HomePage = (
  {}: Props
) => {
  return (
    <ErrorNotifier>
      <div className={styles.pageWrapper}>
        <Topbar/>
      </div>
    </ErrorNotifier>
  )
}