import { ErrorNotifier } from "@/06-shared/lib/errorNotifier";
import styles from "./index.module.css"

type Props = {};

export const HomePage = (
  {}: Props
) => {
  return (
    <ErrorNotifier>
      <div className={styles.pageWrapper}>
        
      </div>
    </ErrorNotifier>
  )
}