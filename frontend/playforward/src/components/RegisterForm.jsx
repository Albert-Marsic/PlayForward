import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
  FieldSeparator,
} from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Link } from "react-router-dom"
import { GOOGLE_LOGIN_URL } from "@/lib/config"

export function RegisterForm({ className, ...props }) {
  const handleGoogleRegister = () => {
    window.location.href = GOOGLE_LOGIN_URL
  }

  return (
    <form className={cn("flex flex-col gap-6", className)} {...props}>
      <FieldGroup>
        <div className="flex flex-col items-center gap-1 text-center">
          <h1 className="text-2xl font-bold">Napravite novi ra??un</h1>
        </div>
        <Field>
          <FieldLabel htmlFor="name">Ime i prezime</FieldLabel>
          <Input id="name" type="text" placeholder="Ime Prezime" required />
        </Field>
        <Field>
          <FieldLabel htmlFor="email">Email</FieldLabel>
          <Input id="email" type="email" placeholder="legionar@example.com" required />
        </Field>
        <Field>
          <FieldLabel htmlFor="password">Password</FieldLabel>
          <Input id="password" type="password" required />
        </Field>
        <Field>
          <FieldLabel htmlFor="confirmPassword">Potvrdi ??ifru</FieldLabel>
          <Input id="confirmPassword" type="password" required />
        </Field>
        <Field>
          <Button type="submit">Registracija</Button>
        </Field>
        <FieldSeparator>Ili se registrirajte sa</FieldSeparator>
        <Field>
          <Button variant="outline" type="button" onClick={handleGoogleRegister}>
            <svg
              className="mr-2 -ml-1 w-4 h-4"
              aria-hidden="true"
              focusable="false"
              data-prefix="fab"
              data-icon="google"
              role="img"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 488 512"
            >
              <path
                fill="currentColor"
                d="M488 261.8C488 403.3 391.1 504 248 504 110.8 504 0 393.2 0 256S110.8 8 248 8c66.8 0 123 24.5 166.3 64.9l-67.5 64.9C258.5 52.6 94.3 116.6 94.3 256c0 86.5 69.1 156.6 153.7 156.6 98.2 0 135-70.4 140.8-106.9H248v-85.3h236.1c2.3 12.7 3.9 24.9 3.9 41.4z"
              ></path>
            </svg>
            Registracija s Googleom
          </Button>
          <FieldDescription className="text-center">
            Ve?? imate ra??un?{" "}
            <Link to="/prijava" className="underline underline-offset-4">
              Prijavite se
            </Link>
          </FieldDescription>
        </Field>
      </FieldGroup>
    </form>
  )
}