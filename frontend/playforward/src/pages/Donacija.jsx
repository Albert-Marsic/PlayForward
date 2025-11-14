import { AddProductionForm } from "@/components/AddProductForm";

export default function AddToy() {
    return (
        <div className="grid lg:grid-cols-2">
            <div className="flex flex-col gap-4 p-6 md:p-10">
                <div className="flex flex-1 items-center justify-center">
                    <div className="w-full max-w-xs">
                        <AddProductionForm />
                    </div>
                </div>
            </div>

            <div className="bg-muted relative hidden lg:block">
                <img 
                    src="/hot-air-balloon.svg"
                    alt="slika"
                    className="absolute inset-0 h-full object-cover dark:brightness-[0.2] dark:grayscale"
                />
            </div>
        </div>
    )
}