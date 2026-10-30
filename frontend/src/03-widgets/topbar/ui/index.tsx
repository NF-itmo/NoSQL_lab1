import { type ReactNode, useEffect, useState } from "react";
import styles from "./index.module.css";
import { Register } from "@/04-features/register-popup";
import { Login } from "@/04-features/login-popup";
import { validateSession } from "@/05-models/auth";
import { logout as logoutReq } from "@/05-models/auth";
import { useErrorNotify } from "@/06-shared/lib/errorNotifier";

type Props = {
  children?: ReactNode | ReactNode[]
};

export const Topbar = ({ children }: Props) => {
  const [showLoginPopup, isLoginPopupShown] = useState<boolean>(false);
  const [showRegisterPopup, isRegisterPopupShown] = useState<boolean>(false);
  const [isAuthed, setIsAuthed] = useState<boolean>(false);
  const showError = useErrorNotify()

  const check = async () => {
    try {
      await validateSession();
      setIsAuthed(true)
    } catch (e) {
      setIsAuthed(false)
    }
  }
  
  useEffect(() => {
    check() 
  }, [])

  const logout = async () => {
    try {
      await logoutReq();
      setIsAuthed(false)
    } catch (error) {
      showError(error)
    }
  }

  return (
    <div className={styles.topbarWrapper}>
      <p className={styles.logoText}>TBD</p>
      <div>
        {children}
      </div>

      <div className={styles.navSection}>
        {
          isAuthed ? (
            <p className={styles.navOption} onClick={() => logout()}>[logout]</p>
          ) : (
            <>
              <p className={styles.navOption} onClick={() => isLoginPopupShown(true)}>[login]</p>
              <p className={styles.navOption} onClick={() => isRegisterPopupShown(true)}>[register]</p>
            </>
          )
        }
      </div>

      <Register
        isOpen={showRegisterPopup}
        onClose={() => {isRegisterPopupShown(false)}}
        onSuccess={() => {isRegisterPopupShown(false); setIsAuthed(true)}}
      />
      <Login
        isOpen={showLoginPopup}
        onClose={() => {isLoginPopupShown(false)}}
        onSuccess={() => {isLoginPopupShown(false); setIsAuthed(true)}}
      />
    </div>
  );
}
