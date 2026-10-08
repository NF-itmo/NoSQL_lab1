import { ReactNode } from "react"
import styles from "./index.module.css"
import { useOutsideClick } from "@/06-shared/lib/useOutsideClick"

type props = {
  name: string
  children: ReactNode | ReactNode[]
  onClose: () => void
}

export const PopupWindow = (
  {
    name,
    children,
    onClose
  } : props
) => {
  const modalRef = useOutsideClick<HTMLDivElement>(() => onClose());

  return (
    <div className={styles.background}>
      <div className={styles.popupWindow} ref={modalRef}>
        <div className={styles.popupHead}>
          <p className={styles.popupName}>{name}</p>
          <p className={styles.popupClose} onClick={() => onClose()}>[x]</p>
        </div>
        <div className={styles.popupBody}>
          {children}
        </div>
      </div>
    </div>
  )
}