import styles from "./import.module.css"

type props = {
  name: string
  onClick: () => void
}

export const PopupButton = (
  {
    name,
    onClick
  } : props
) => {
  return (
    <div className={styles.popupButton} onClick={() => onClick()}>
      {name}
    </div>
  )
}